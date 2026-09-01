package com.homeserver.util.docker;
import org.springframework.stereotype.Service;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;
import com.homeserver.util.ApiResponse;
import com.github.dockerjava.api.model.ContainerPort;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DockerService {

    private static final Logger log = LoggerFactory.getLogger(DockerService.class);

    private final DockerClient dockerClient;

    // CopyOnWriteArrayList: safe for the "infrequent writes, frequent reads" pattern
    // a periodic refresh + many dashboard clients reading is exactly that pattern.
    private final List<ContainerInfo> containers = new CopyOnWriteArrayList<>();

    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @PostConstruct
    public void init() {
        log.info("Loading DockerService");
        refreshContainers();
    }

    // Runs every 5s
    @Scheduled(fixedRate = 5000)
    public void refreshContainers() {
        try {
            List<Container> dockerContainers = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .exec();

            List<ContainerInfo> updated = new ArrayList<>();

            for (Container c : dockerContainers) {
                ContainerInfo info = new ContainerInfo();
                info.setId(c.getId());

                if (c.getNames() != null && c.getNames().length > 0) {
                    info.setName(c.getNames()[0].replace("/", ""));
                }

                info.setState(c.getState()); // gets the state of the container
                info.setStatus(c.getStatus()); // get the status of the container

                // get the private port for the container
                ContainerPort[] ports = c.getPorts();
                if (ports != null && ports.length > 1 && ports[0] != null){
                    info.setPort(Integer.toString(ports[0].getPublicPort()));
                }else{
                    info.setPort(null);
                }
                

                updated.add(info);
            }

            // Swap contents in one shot rather than clear()+rebuild in place,
            // so readers never see a half-empty list mid-refresh.
            containers.clear();
            containers.addAll(updated);

        } catch (Exception e) {
            // Don't let a Docker hiccup take down the whole scheduled task
            // (or, if this were still in @PostConstruct, the whole app context).
            log.error("Failed to refresh containers", e);
        }
    }

    public List<ContainerInfo> getContainers() {
        // Return an unmodifiable view so callers can't mutate internal state.
        return Collections.unmodifiableList(containers);
    }

    public ResponseEntity<List<ContainerInfo>> getApplications() {
        // returning a list of container Info but only with the application flag on true
        List<ContainerInfo> applications = new ArrayList<>();
        for (ContainerInfo c :containers){
            if (c.getApplication() == true){
                applications.add(c);
            }
        }
        
        return ResponseEntity.ok(applications);
    }

    // helper function
    public ContainerInfo getSingleContainer(String id) {
        for (ContainerInfo c : containers) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    //helper function
    public boolean containerExists(String id) {
        return getSingleContainer(id) != null;
    }

    // helper function
    public String getContainerStatus(String id) {
        ContainerInfo container = getSingleContainer(id);
        return container == null ? "Container does not exist" : container.getStatus();
    }

    public ResponseEntity<ApiResponse> performAction(String id, String action) {
        if (!containerExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Container not found: " + id));
        }

        try {
            switch (action.toLowerCase()) {
                case "start":
                    dockerClient.startContainerCmd(id).exec();
                    refreshContainers();
                    return ResponseEntity.ok(new ApiResponse(true, "Container has been started"));
                case "stop":
                    dockerClient.stopContainerCmd(id).exec();
                    refreshContainers();
                    return ResponseEntity.ok(new ApiResponse(true, "Container has been stopped"));
                case "restart":
                    dockerClient.restartContainerCmd(id).exec();
                    refreshContainers();
                    return ResponseEntity.ok(new ApiResponse(true, "Container has been restarted"));
                default:
                    return ResponseEntity.badRequest().body(new ApiResponse(false, "Unknown action: " + action));
            }
        } catch (NotFoundException e) {
            // race: container disappeared between the exists-check and the actual call
            log.warn("Container {} vanished before action '{}' could complete", id, action);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Container not found: " + id));
        } catch (NotModifiedException | ConflictException e) {
            log.warn("Action '{}' had no effect on container {} (state conflict)", action, id);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, "Container is already in the requested state or cannot transition"));
        } catch (Exception e) {
            log.error("Failed to perform action '{}' on container {}", action, id, e);
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "An error has happened"));
        }
    }
}