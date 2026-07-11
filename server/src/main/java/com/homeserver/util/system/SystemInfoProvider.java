package com.homeserver.util.system;
import com.homeserver.util.system.ServerInfo;

import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;
import oshi.hardware.CentralProcessor;

import oshi.hardware.GlobalMemory;
import oshi.util.FormatUtil;

//** Class is all about getting the needed information to get the needed data to fill in the SystemInfo class 
// class does not handle any storing of data just getting of data*/

public class SystemInfoProvider {
    SystemInfo si = new SystemInfo();
    OperatingSystem os = si.getOperatingSystem();
    CentralProcessor cpu = si.getHardware().getProcessor();

    public ServerInfo load(){
        // returns a ServerInfo class of the current systems info
        ServerInfo info = new ServerInfo(
            getCpuModel(),
            getCpuCore(),
            getTotalMemory(),
            getOsName(),
            getOsVersion()
        );
        return info;
    }

    public String getCpuModel() {
        // returns the systems cpu name
        return cpu.getModelName();

    }

    public int getCpuCore() {
        // returns the systems cpu cores
        return cpu.getPhysicalProcessorCount();
    }

    public long getTotalMemory() {
        // returns the total memory of the system
    }

    public String getOsName() {
        // returns the operator name of the system
        return os.getName();
    }

    public String getOsVersion() {
        // returns the OS version of the system
        return os.getVersionInfo().getVersion();
    }
}