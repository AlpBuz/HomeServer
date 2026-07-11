package com.homeserver.homeserver.util.system;
import com.homeserver.homeserver.util.system.SystemMetric;

public class SystemMetricProvider {
    public SystemMetric load() {
        SystemMetric metric = new SystemMetric(getCpuMetric(), getCpuMetric());
        return metric;
    }

    public double getCpuMetric() {
        // gets the current cpu usuage metric
    }

    public double getMemoryMetric() {
        // gets the current memory usuage metric
    }
}