package com.homeserver.homeserver.util.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

                info.setImage(c.getImage());
                info.setState(c.getState());
                info.setStatus(c.getStatus());

                // Only bother pulling live stats for running containers —
                // stats calls on stopped containers just error out.
                if ("running".equalsIgnoreCase(c.getState())) {
                    populateStats(info);
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

    private void populateStats(ContainerInfo info) {
        try {
            StatsCmd statsCmd = dockerClient.statsCmd(info.getId());
            statsCmd.withNoStream(true); // single snapshot, not an ongoing stream

            CountDownLatch latch = new CountDownLatch(1);
            Statistics[] result = new Statistics[1];

            statsCmd.exec(new ResultCallback.Adapter<Statistics>() {
                @Override
                public void onNext(Statistics stats) {
                    result[0] = stats;
                    latch.countDown();
                }
            });

            latch.await(3, TimeUnit.SECONDS);
            statsCmd.close();

            Statistics stats = result[0];
            if (stats == null || stats.getCpuStats() == null || stats.getPreCpuStats() == null) {
                return;
            }

            long cpuDelta = stats.getCpuStats().getCpuUsage().getTotalUsage()
                    - stats.getPreCpuStats().getCpuUsage().getTotalUsage();
            long systemDelta = stats.getCpuStats().getSystemCpuUsage()
                    - stats.getPreCpuStats().getSystemCpuUsage();

            Long onlineCpusRaw = stats.getCpuStats().getOnlineCpus();
            int onlineCpus = onlineCpusRaw != null ? onlineCpusRaw.intValue() : 1;

            if (systemDelta > 0 && cpuDelta > 0) {
                double cpuPercent = ((double) cpuDelta / systemDelta) * onlineCpus * 100.0;
                info.setCpuUsage(cpuPercent);
            }

            if (stats.getMemoryStats() != null) {
                if (stats.getMemoryStats().getUsage() != null) {
                    info.setMemoryUsage(stats.getMemoryStats().getUsage());
                }
                if (stats.getMemoryStats().getLimit() != null) {
                    info.setMemoryLimit(stats.getMemoryStats().getLimit());
                }
            }

        } catch (Exception e) {
            log.warn("Failed to get stats for container {}", info.getId(), e);
        }
    }

    public List<ContainerInfo> getContainers() {
        // Return an unmodifiable view so callers can't mutate internal state.
        return Collections.unmodifiableList(containers);
    }

    public ContainerInfo getSingleContainer(String id) {
        for (ContainerInfo c : containers) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public boolean containerExists(String id) {
        return getSingleContainer(id) != null;
    }

    public String getContainerStatus(String id) {
        ContainerInfo container = getSingleContainer(id);
        return container == null ? "Container does not exist" : container.getStatus();
    }

    public String performAction(String id, String action) {
        if (!containerExists(id)) {
            return "container does not exist";
        }

        try {
            switch (action.toLowerCase()) {
                case "start":
                    dockerClient.startContainerCmd(id).exec();
                    refreshContainers();
                    return "container started";
                case "stop":
                    dockerClient.stopContainerCmd(id).exec();
                    refreshContainers();
                    return "container stopped";
                case "restart":
                    dockerClient.restartContainerCmd(id).exec();
                    refreshContainers();
                    return "container has been restarted";
                default:
                    return "unknown action so no action performed on container";
            }
        } catch (Exception e) {
            log.error("Failed to perform action '{}' on container {}", action, id, e);
            return "action failed: " + e.getMessage();
        }
    }
}