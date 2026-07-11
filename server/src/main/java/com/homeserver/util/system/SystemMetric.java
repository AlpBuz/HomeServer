package com.homeserver.util.system;

public class SystemMetric {
    private double CpuMetric;
    private double MemoryMetric;

    @Getter
    public SystemMetric(double CpuMetric, double MemoryMetric) {
        this.CpuMetric = CpuMetric;
        this.MemoryMetric = MemoryMetric;
    }
}