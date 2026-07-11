package com.homeserver.homeserver.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homeserver.homeserver.util.system.ServerService;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    private final ServerService serverService;

    public SystemController(ServerService serverService) {
        this.serverService = serverService;
    }

    @GetMapping("info")
    public ServerInfo getServerInfo() {
        // gets the servers static info such as cpu model, cores, max memory and such
        ServerInfo info = serverService.getServerInfo();
        return info;

        // get cpu model

        // get cores

        // get max memory

        //get os name and version

        // create serverinfo class object

        // return the class object
    }

    @GetMapping("metrics")
    public SystemMetric refreashMetrics(){
        // gets the current status of the servers metrics
        // such as cpu usage, memory usuage and such

        // refreash the metrics first
        serverService.refreashMetrics();

        SystemMetric metrics = serverService.getSystemMetrics();
        return metrics;
    }
}