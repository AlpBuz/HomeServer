package com.homeserver.homeserver.controller;

import com.homeserver.homeserver.util.ContainerInfo;
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
@RequestMapping("/docker")
public class DockerController {

    @GetMapping("/status")
    public List<ContainerInfo> status() throws IOException, InterruptedException {
        List<ContainerInfo> containers = new ArrayList<>();

        ProcessBuilder pb = new ProcessBuilder(
                "docker",
                "ps",
                "-a",
                "--format",
                "{{.ID}}|{{.Names}}|{{.Image}}|{{.State}}|{{.Status}}"
        );

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|", 5);

            if (parts.length == 5) {
                containers.add(new ContainerInfo(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4]
                ));
            }
        }

        process.waitFor();

        return containers;
    }

    @PostMapping("/{id}/start")
    public String start(@PathVariable String id) throws Exception {

        ProcessBuilder pb = new ProcessBuilder(
                "docker",
                "start",
                id
        );

        pb.start().waitFor();

        return "Started " + id;
    }

    @PostMapping("/{id}/stop")
    public String stop(@PathVariable String id) throws Exception {

        ProcessBuilder pb = new ProcessBuilder(
                "docker",
                "stop",
                id
        );

        pb.start().waitFor();

        return "Stopped " + id;
    }

    @PostMapping("/{id}/restart")
    public String restart(@PathVariable String id) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "docker",
                "restart",
                id
        );

        pb.start().waitFor();

        return "Restarted " + id;
    }

}