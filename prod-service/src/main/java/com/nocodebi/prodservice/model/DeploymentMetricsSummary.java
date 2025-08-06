package com.nocodebi.prodservice.model;

public class DeploymentMetricsSummary {
    private String deploymentName;
    private String avgCpu;     // e.g., "250m"
    private String avgMemory;  // e.g., "150Mi"

    public DeploymentMetricsSummary(String deploymentName, String avgCpu, String avgMemory) {
        this.deploymentName = deploymentName;
        this.avgCpu = avgCpu;
        this.avgMemory = avgMemory;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String getAvgCpu() {
        return avgCpu;
    }

    public void setAvgCpu(String avgCpu) {
        this.avgCpu = avgCpu;
    }

    public String getAvgMemory() {
        return avgMemory;
    }

    public void setAvgMemory(String avgMemory) {
        this.avgMemory = avgMemory;
    }
}
