package com.homeserver.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.homeserver.util.system.ServerService;
import com.homeserver.util.system.ServerInfo;
import com.homeserver.util.system.SystemMetric;

import ch.qos.logback.core.util.SystemInfo;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    private final ServerService serverService;

    public SystemController(ServerService serverService) {
        this.serverService = serverService;
    }

    @GetMapping("info")
    public ResponseEntity<ServerInfo> getServerInfo() {
        // gets the servers static info such as cpu model, cores, max memory and such
        ServerInfo info = serverService.getServerInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("metrics")
    public ResponseEntity<SystemMetric> refreashMetrics(){
        // gets the current status of the servers metrics
        // such as cpu usage, memory usuage and such

        // refreash the metrics first
        serverService.refreashMetrics();

        SystemMetric metrics = serverService.getSystemMetrics();
        return ResponseEntity.ok(metrics);
    }
}