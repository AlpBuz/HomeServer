package com.homeserver.homeserver.util.system;

import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import com.homeserver.util.Util;
import oshi.software.os.OperatingSystem;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

/** Class is all about getting the needed information to fill in the ServerInfo object.
 *  It just handles getting the data and creating the ServerInfo object. */
@Component
public class SystemInfoProvider {
    private final SystemInfo systemInfo = new SystemInfo();
    private final OperatingSystem os = systemInfo.getOperatingSystem();
    private final CentralProcessor cpu = systemInfo.getHardware().getProcessor();
    private final GlobalMemory memory = systemInfo.getHardware().getMemory();

    public ServerInfo load() {
        return new ServerInfo(
            getCpuModel(),
            getCpuCore(),
            getTotalMemory(),
            getOsName(),
            getOsVersion()
        );
    }

    private String getCpuModel() {
        return cpu.getProcessorIdentifier().getName();
    }

    private int getCpuCore() {
        return cpu.getPhysicalProcessorCount();
    }

    private long getTotalMemory() {
        return Util.formatBytes(memory.getTotal());
    }

    private String getOsName() {
        return os.getFamily();
    }

    private String getOsVersion() {
        return os.getVersionInfo().getVersion();
    }
}