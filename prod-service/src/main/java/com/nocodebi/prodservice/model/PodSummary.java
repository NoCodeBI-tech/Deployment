package com.nocodebi.prodservice.model;

public class PodSummary {
    public String name;
    public String ready;
    public String status;
    public String restarts;
    public String age;

    public PodSummary() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReady() {
        return ready;
    }

    public void setReady(String ready) {
        this.ready = ready;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRestarts() {
        return restarts;
    }

    public void setRestarts(String restarts) {
        this.restarts = restarts;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
