package com.homeserver.homeserver.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
1. 📊 System Overview

This file will handle all the request about the current servers information like:
CPU usage
RAM usage
Disk usage (very important)
Uptime
Network upload/download (optional but impressive)

Example:
CPU: 12%
RAM: 4.2 / 16 GB
Disk: 300 / 512 GB
Uptime: 5 days

*Should handle
*/



@RestController
@RequestMapping("/api/system")
public class SystemController {
    // @GetMapping("getResources")
    // public ResourceService getResources() {
        
    // }

    // @GetMapping("refreashMetrics")
    // public ResourceService refreashMetrics(){

    // }
}