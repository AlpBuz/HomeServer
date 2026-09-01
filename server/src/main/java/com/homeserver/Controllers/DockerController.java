package com.homeserver.Controllers;
import com.homeserver.util.docker.DockerService;
import com.homeserver.util.docker.ContainerInfo;
import com.homeserver.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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

    // @GetMapping("/getContainers")
    // public List<ContainerInfo> status() {
    //     return dockerService.getContainers();
    // }

    @GetMapping("/getApplications")
    public ResponseEntity<List<ContainerInfo>> getApplications() {
        return dockerService.getApplications();
    }

    @PostMapping("/{id}/start") // starts the given container
    public ResponseEntity<ApiResponse> start(@PathVariable String id) throws Exception {
        return dockerService.performAction(id, "start");
    }

    @PostMapping("/{id}/stop") // stops the given container
    public ResponseEntity<ApiResponse> stop(@PathVariable String id) throws Exception {
        return dockerService.performAction(id, "stop");
    }

    @PostMapping("/{id}/restart") // restarts the given container
    public ResponseEntity<ApiResponse> restart(@PathVariable String id) throws Exception {
        return dockerService.performAction(id, "restart");
    }

}