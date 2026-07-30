package com.homeserver.Controllers;
import com.homeserver.util.docker.DockerService;
import com.homeserver.util.docker.ContainerInfo;
import com.homeserver.util.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/getApplications")
    public List<ContainerInfo> getApplications() {
        return dockerService.getApplications();
    }

    @PostMapping("/{id}/start") // starts the given container
    public ApiResponse start(@PathVariable String id) throws Exception {
        ApiResponse response = dockerService.performAction(id, "start");
        return response;
    }

    @PostMapping("/{id}/stop") // stops the given container
    public ApiResponse stop(@PathVariable String id) throws Exception {
        ApiResponse response = dockerService.performAction(id, "stop");
        return response;
    }

    @PostMapping("/{id}/restart") // restarts the given container
    public ApiResponse restart(@PathVariable String id) throws Exception {
        ApiResponse response = dockerService.performAction(id, "restart");
        
        return response;
    }

}