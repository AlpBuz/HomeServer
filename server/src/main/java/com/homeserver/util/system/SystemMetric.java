package com.homeserver.homeserver.util.system;

public class SystemMetric {
    private final double cpuMetric;
    private final double memoryMetric;

    public SystemMetric(double cpuMetric, double memoryMetric) {
        this.cpuMetric = cpuMetric;
        this.memoryMetric = memoryMetric;
    }

    public double getCpuMetric() { return cpuMetric; }
    public double getMemoryMetric() { return memoryMetric; }
}