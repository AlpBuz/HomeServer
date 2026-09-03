package com.homeserver.util.alert;

import com.homeserver.util.docker.ContainerInfo;
import com.homeserver.util.docker.DockerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final DockerService dockerService;
    private final JavaMailSender mailSender;
    private final AlertEmailStore emailStore;
    private final String fromAddress;
    private final Duration renotifyInterval;

    // container id -> alert state, so a container that's still down doesn't get re-emailed every check
    private final Map<String, AlertInfo> containerAlerts = new HashMap<>();

    public AlertService(DockerService dockerService,
                         JavaMailSender mailSender,
                         AlertEmailStore emailStore,
                         @Value("${alert.mail.from:${spring.mail.username:}}") String fromAddress,
                         @Value("${alert.renotify-minutes:30}") long renotifyMinutes) {
        this.dockerService = dockerService;
        this.mailSender = mailSender;
        this.emailStore = emailStore;
        this.fromAddress = fromAddress;
        this.renotifyInterval = Duration.ofMinutes(renotifyMinutes);
    }

    // Runs on its own cadence, slightly slower than DockerService's 5s container refresh
    // so it's always checking against a fresh list.
    @Scheduled(fixedRate = 30000)
    public void checkContainers() {
        List<ContainerInfo> containers = dockerService.getContainers();
        List<String> downContainers = new ArrayList<>();

        for (ContainerInfo c : containers) {
            if (!"running".equalsIgnoreCase(c.getState())) {
                downContainers.add(c.getId());
            }
        }

        alertMessage(downContainers);
    }

    public boolean checkEmail() {
        return emailStore.isSet();
    }

    // Safe to expose over the API - hides all but the first character of the local part.
    public String getMaskedEmail() {
        return AlertEmailStore.mask(emailStore.get());
    }

    // Emails the owner about any newly-down (or still-down past the cooldown) containers,
    // and sends a recovery notice for anything that was down and is now running again.
    public void alertMessage(List<String> downContainers) {
        if (!checkEmail()) {
            return;
        }

        Set<String> stillDown = new HashSet<>(downContainers);

        for (String id : downContainers) {
            ContainerInfo container = dockerService.getSingleContainer(id);
            if (container == null) {
                continue;
            }

            AlertInfo info = containerAlerts.computeIfAbsent(id, key -> new AlertInfo(id, container.getName()));
            info.setContainerName(container.getName());

            boolean cooldownElapsed = info.getLastAlertSent() == null
                    || Duration.between(info.getLastAlertSent(), Instant.now()).compareTo(renotifyInterval) >= 0;

            if (!info.isDown() || cooldownElapsed) {
                sendMail("[HomeServer] " + container.getName() + " is down",
                        "Container '" + container.getName() + "' is currently " + container.getState() + ".\n"
                                + "Status: " + container.getStatus());
                info.setLastAlertSent(Instant.now());
            }
            info.setDown(true);
        }

        // anything we were tracking as down that isn't in this round's downContainers has recovered
        for (AlertInfo info : containerAlerts.values()) {
            if (info.isDown() && !stillDown.contains(info.getContainerId())) {
                sendMail("[HomeServer] " + info.getContainerName() + " is back up",
                        "Container '" + info.getContainerName() + "' is running again.");
                info.setDown(false);
                info.setLastAlertSent(null);
            }
        }
    }

    private void sendMail(String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailStore.get());
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            // Don't let a bad SMTP config or a transient send failure take down the scheduled check.
            log.error("Failed to send alert email", e);
        }
    }

    public void updateEmail(String email) {
        emailStore.save(email);
    }

    public void clearEmail() {
        emailStore.clear();
    }
}
