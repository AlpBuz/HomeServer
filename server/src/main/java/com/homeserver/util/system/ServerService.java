package com.homeserver.homeserver.util.system;

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

    public SystemMetric getSystemMetrics() {
        // returns the metrics info
        return metrics;
    }

    

}