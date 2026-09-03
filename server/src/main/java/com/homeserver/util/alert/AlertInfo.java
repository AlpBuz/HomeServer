package com.homeserver.util.alert;

import java.time.Instant;

// Tracks the alerting state of a single container so a container that stays down
// doesn't get re-emailed on every check, and so we know when to send a recovery notice.
public class AlertInfo {
    private final String containerId;
    private String containerName;
    private boolean down;
    private Instant lastAlertSent;

    public AlertInfo(String containerId, String containerName) {
        this.containerId = containerId;
        this.containerName = containerName;
    }

    public String getContainerId() { return containerId; }
    public String getContainerName() { return containerName; }
    public void setContainerName(String containerName) { this.containerName = containerName; }
    public boolean isDown() { return down; }
    public void setDown(boolean down) { this.down = down; }
    public Instant getLastAlertSent() { return lastAlertSent; }
    public void setLastAlertSent(Instant lastAlertSent) { this.lastAlertSent = lastAlertSent; }
}
