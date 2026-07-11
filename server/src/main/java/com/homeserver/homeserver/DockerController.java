package com.homeserver.homeserver;
import com.homeserver.homeserver.util.docker.DockerService;
import com.homeserver.homeserver.util.docker.ContainerInfo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/docker")
public class DockerController {

    private final DockerService dockerService;

    public DockerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @GetMapping("/getContainers")
    public List<ContainerInfo> status() {
        return dockerService.getContainers();
    }

    @PostMapping("/{id}/start") // starts the given container
    public String start(@PathVariable String id) throws Exception {
        String message = dockerService.performAction(id, "start");
        return message;
    }

    @PostMapping("/{id}/stop") // stops the given container
    public String stop(@PathVariable String id) throws Exception {
        String message = dockerService.performAction(id, "stop");
        return message;
    }

    @PostMapping("/{id}/restart") // restarts the given container
    public String restart(@PathVariable String id) throws Exception {
        String message = dockerService.performAction(id, "restart");
        return message;
    }

}