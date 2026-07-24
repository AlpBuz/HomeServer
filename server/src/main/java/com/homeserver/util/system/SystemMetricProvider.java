package com.homeserver.util.system;

import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

@Component
public class SystemMetricProvider {
    private final SystemInfo systemInfo = new SystemInfo();
    private final CentralProcessor cpu = systemInfo.getHardware().getProcessor();
    private final GlobalMemory memory = systemInfo.getHardware().getMemory();

    private long[] prevTicks = cpu.getSystemCpuLoadTicks();

    public SystemMetric load() {
        return new SystemMetric(getCpuMetric(), getMemoryMetric());
    }

    private double getCpuMetric() {
        // load since the last time ticks were captured, then reset the baseline
        double load = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = cpu.getSystemCpuLoadTicks();

        double cpuPercent = Math.round(load * 100) / 100.0;
        return cpuPercent;
    }

    private double getMemoryMetric() {
        long total = memory.getTotal();
        long available = memory.getAvailable();
        double percent = ((double) (total - available) / total) * 100;
        return Math.round(percent * 100.0) / 100.0;
    }
}