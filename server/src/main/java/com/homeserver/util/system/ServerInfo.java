package com.homeserver.util.system;
//** Class is all about storing information about the current system. Does not do any calls to obtain information about the server */
public class ServerInfo {
    private String CpuModel;
    private int CpuCore;
    private long totalMemory;
    private String osName;
    private String osVersion;

    @Getter
    public ServerInfo(String CpuModel, int CpuCore, long totalMemory, String osName, String osVersion) {
        this.CpuModel = CpuModel;
        this.CpuCore = CpuCore;
        this.totalMemory = totalMemory
        this.osName = osName;
        this.osVersion = osVersion;
    }
}