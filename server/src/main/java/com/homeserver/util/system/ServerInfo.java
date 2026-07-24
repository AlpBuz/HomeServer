package com.homeserver.util.system;

public class ServerInfo {
    private final String cpuModel;
    private final int cpuCore;
    private final long totalMemory;
    private final String osName;
    private final String osVersion;

    public ServerInfo(String cpuModel, int cpuCore, long totalMemory, String osName, String osVersion) {
        this.cpuModel = cpuModel;
        this.cpuCore = cpuCore;
        this.totalMemory = totalMemory;
        this.osName = osName;
        this.osVersion = osVersion;
    }

    public String getCpuModel() { return cpuModel; }
    public int getCpuCore() { return cpuCore; }
    public long getTotalMemory() { return totalMemory; }
    public String getOsName() { return osName; }
    public String getOsVersion() { return osVersion; }
}