package com.homeserver.homeserver.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    private String cpu;
    private int cpuCores;
    private double cpuUsage;
    private int memoryLimit;
    private double memoryUsuage;
    private String ipAddress;
    private double diskUsage;
    private int diskLimit;

    @PostConstruct
    public void init() {
        System.out.println("Loading ResourceService class");

        // get information that never changes such as CPU name, memory limit, cpu cores and what ever you can get about the information on
        // the server

        // call the refreash function to obtain the information of variables that do change such as cpu usage and memory usuage
    }

    public void refreashMetric(){
        //**Function refreashes the metrics that constanly change */
    }






    // ----- methods to return the class variables
    public String getCPU(){
        return cpu;
    }

    public int getCPUCores(){
        return cpuCores;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public int getmemoryLimit(){
        return memoryLimit;
    }

    public double getmemoryUsuage() {
        return memoryUsuage;
    }

    public String getipAddress() {
        return ipAddress;
    }

    public double getdiskUsage () {
        return diskUsage;
    }

    public int getdiskLimit () {
        return diskLimit;
    }

}