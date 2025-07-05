package com.nocodebi.service.model;

public class PodStatusInfo {
    private String name;
    private String phase;
    private String reason;
    private String message;

    public PodStatusInfo(String name, String phase, String reason, String message) {
        this.name = name;
        this.phase = phase;
        this.reason = reason;
        this.message = message;
    }

    // Getters & Setters
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPhase() { return phase; }

    public void setPhase(String phase) { this.phase = phase; }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
