package com.homeserver.homeserver;
import com.homeserver.homeserver.util.docker.DockerService;
import com.homeserver.homeserver.util.docker.ContainerInfo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.HashMap;
import java.util.Map;
/*
Handles all the redirects that happens to the different applcaiton running on the server
Since all applications will be inside containers on this home server than we need to check if container exist and it is running
*/


@RestController
@RequestMapping("/api/redirect")
public class Redirects {
    // HashMap of all the redirect links for each current container in the system
    private final HashMap<String, String> redirects = new HashMap<>();
    private final DockerService dockerService;

    public Redirects(DockerService dockerService) {
        this.dockerService = dockerService;
        redirects.put("jellyfin", "http://jellyfin:8096");
        redirects.put("nextcloud", "http://nextcloud:9000");
    }

    @GetMapping("/{id}")
    public Map<String, String> redirect(@PathVariable String id) {
        // check if the container exists
        ContainerInfo container = dockerService.getSingleContainer(id);
        if (container == null){
            return Map.of("error", "container does not exist");
        }
        
        // check if the container is currently not running
        if (!container.getState().equals("running")){
            return Map.of("error", "Container is not running");
        }

        // redirect the user or send an error message depending on the error
        String url = redirects.get(container.getName());
        return Map.of("url", url);
    }
}