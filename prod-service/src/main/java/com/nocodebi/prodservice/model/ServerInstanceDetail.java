package com.nocodebi.prodservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ServerInstanceDetail {

    private String name;

    private String type;

    private boolean isRunning;

    private String ram;

    private int numberOfInstance;

    private String core;

    private boolean isAutoScalingEnabled;

    private String cpuUsage;

    private String usedRam;

    private String usedCore;

    public ServerInstanceDetail() {

        this.name = "";

        this.type = "";

        this.isRunning = false;

        this.numberOfInstance = 0;

        this.ram = "";

        this.core = "";

        this.isAutoScalingEnabled = false;

        this.cpuUsage = "";

        this.usedCore = "";

        this.usedRam = "";

    }

    public int getNumberOfInstance() {

        return numberOfInstance;

    }

    public void setNumberOfInstance(int numberOfInstance) {

        this.numberOfInstance = numberOfInstance;

    }

    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }

    public String getType() {

        return type;

    }

    public void setType(String type) {

        this.type = type;

    }

    public boolean isRunning() {

        return isRunning;

    }

    public void setRunning(boolean running) {

        isRunning = running;

    }

    public String getRam() {

        return ram;

    }

    public void setRam(String ram) {

        this.ram = ram;

    }

    public String getCore() {

        return core;

    }

    public void setCore(String core) {

        this.core = core;

    }

    public boolean isAutoScalingEnabled() {

        return isAutoScalingEnabled;

    }

    public void setAutoScalingEnabled(boolean autoScalingEnabled) {

        isAutoScalingEnabled = autoScalingEnabled;

    }

    public String getCpuUsage() {

        return cpuUsage;

    }

    public void setCpuUsage(String cpuUsage) {

        this.cpuUsage = cpuUsage;

    }

    public String getUsedRam() {
        return usedRam;
    }

    public void setUsedRam(String usedRam) {
        this.usedRam = usedRam;
    }

    public String getUsedCore() {
        return usedCore;
    }

    public void setUsedCore(String usedCore) {
        this.usedCore = usedCore;
    }
}

