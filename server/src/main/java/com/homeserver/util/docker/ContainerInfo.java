package com.homeserver.util.docker;
import java.util.Set;

public class ContainerInfo {

    private static final Set<String> NON_APPLICATION_CONTAINERS = Set.of(
        "postgres",
        "nginx",
        "docker-proxy",
        "server",
        "hub"
    );

    private String id;
    private String name;
    private String state;
    private String status;
    private boolean application;

    public ContainerInfo() {
    }

    public ContainerInfo(String id, String name, String state, String status, boolean application) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getState() { return state; }
    public String getStatus() { return status; }
    public boolean getApplication() { return application; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { 
        this.name = name;
        this.application = !NON_APPLICATION_CONTAINERS.contains(name);
    }
    public void setState(String state) { this.state = state; }
    public void setStatus(String status) { this.status = status; }
}