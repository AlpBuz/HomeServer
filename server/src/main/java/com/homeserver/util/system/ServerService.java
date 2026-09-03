package com.homeserver.util.system;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;


@Service
public class ServerService {
    private static final Logger log = LoggerFactory.getLogger(ServerService.class);

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
        logMetricsSource();
        info = InfoProvider.load();
        refreashMetrics();
    }

    // Hardware metrics only reflect the real host when the oshi.util.proc.path/sys.path
    // system properties point at a host-mounted /proc and /sys (see docker-compose.yml's
    // JAVA_TOOL_OPTIONS + volumes for the "server" service). Without that, oshi falls back
    // to whatever /proc and /sys this container itself sees, which is not the physical host.
    private void logMetricsSource() {
        String procPath = System.getProperty("oshi.util.proc.path");
        String sysPath = System.getProperty("oshi.util.sys.path");

        if (procPath == null || sysPath == null) {
            log.warn("oshi.util.proc.path/oshi.util.sys.path are not set - system metrics will reflect "
                    + "this container's own view, not the physical host. Set JAVA_TOOL_OPTIONS to point at "
                    + "a host-mounted /proc and /sys, as docker-compose.yml does for the 'server' service.");
            return;
        }

        boolean procMounted = Files.isDirectory(Path.of(procPath));
        boolean sysMounted = Files.isDirectory(Path.of(sysPath));

        if (procMounted && sysMounted) {
            log.info("Reading system metrics from host-mounted {} and {}", procPath, sysPath);
        } else {
            log.warn("oshi is configured to read {} and {}, but one or both aren't mounted - metrics may "
                    + "be inaccurate or unavailable. Check the /proc and /sys volume mounts.", procPath, sysPath);
        }
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