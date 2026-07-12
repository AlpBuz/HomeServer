package com.homeserver.homeserver.util.docker;

public class ContainerInfo {

    private String id;
    private String name;
    private String image;
    private String state;
    private String status;
    private double cpuUsage;
    private long memoryUsage;
    private long memoryLimit;

    public ContainerInfo() {
    }

    public ContainerInfo(String id, String name, String image, String state, String status) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.state = state;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getState() { return state; }
    public String getStatus() { return status; }
    public double getCpuUsage() { return cpuUsage; }
    public long getMemoryUsage() { return memoryUsage; }
    public long getMemoryLimit() { return memoryLimit; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImage(String image) { this.image = image; }
    public void setState(String state) { this.state = state; }
    public void setStatus(String status) { this.status = status; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    public void setMemoryUsage(long memoryUsage) { this.memoryUsage = memoryUsage; }
    public void setMemoryLimit(long memoryLimit) { this.memoryLimit = memoryLimit; }
}