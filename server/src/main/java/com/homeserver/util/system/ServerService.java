package com.homeserver.homeserver.util.system;

import com.homeserver.homeserver.util.system.SystemInfoProvider;
import com.homeserver.homeserver.util.system.ServerInfo;

import com.homeserver.homeserver.util.system.SystemMetric;
import com.homeserver.homeserver.util.system.SystemMetricProvider;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;


@Service
public class ServerService {
    private final SystemInfoProvider InfoProvider; // variable holds the system provider
    private final SystemMetricProvider MetricProvider; // variable holders the system metric provider

    private ServerInfo info; // this variable will carry the information of the current server
    private SystemMetric metrics; // variable that carries the metrics of the current system

    public ServerService(SystemInfoProvider InfoProvider, SystemMetricProvider MetricProvider) {
        this.InfoProvider = InfoProvider;
        this.MetricProvider = MetricProvider;
    }

    @PostConstruct
    public void init() {
        // function will run once to help load the needed and current metrics
        info = InfoProvider.load();
        refreashMetrics();
    }

    public void refreashMetrics() {
        // refreash the outdated metrics to get new values
        metrics = MetricProvider.load();
    }

    public ServerInfo getServerInfo() {
        // return the systems info
        return info;
    }

    public getSystemMetrics() {
        // returns the metrics info
        return metrics;
    }

    

}