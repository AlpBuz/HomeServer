package com.homeserver.homeserver.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DockerService {

    private final DockerClient dockerClient;
    private final List<ContainerInfo> containers = new ArrayList<>();

    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @PostConstruct
    public void init() {
        System.out.println("Loading DockerService class");
        refreshContainers();
    }

    public void refreshContainers() {

        containers.clear();

        List<Container> dockerContainers = dockerClient.listContainersCmd().withShowAll(true).exec();

        for (Container c : dockerContainers) {

            ContainerInfo info = new ContainerInfo();

            info.setId(c.getId());

            if (c.getNames() != null && c.getNames().length > 0) {
                info.setName(c.getNames()[0].replace("/", ""));
            }

            info.setImage(c.getImage());
            info.setState(c.getState());
            info.setStatus(c.getStatus());

            containers.add(info);
        }
    }

    public List<ContainerInfo> getContainers() {
        return containers;
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

        if (container == null) {
            return "Container does not exist";
        }

        return container.getStatus();
    }

    public String performAction(String id, String action){
        // check if container exist
        ContainerInfo container = getSingleContainer(id);
        if (container == null){
            return "container does not exist";
        }

        // perform the needed action
        if (action.equals("start")){
            // perform start action

            return "container started";
        }else if (action.equals("stop")){
            // perform stop action on container

            return "container stopped";
        }else if (action.equals("restart")){
            // perform reset action on container

            return "container has been restarted";
        }else{
            // unknown action return an error
            return "unknown action so no action performed on container";
        }

    }
}