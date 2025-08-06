package com.nocodebi.prodservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StageUser {

    private String userID;
    private String assignDate;
    private List<String> roles; // Possible roles: collaborator, license, deployment, autoscaling, admin

    public void setDefault() {
        this.userID = "";
        this.assignDate = "";
        this.roles = new ArrayList<>(); // Initialize with an empty list
    }

    // Getters and Setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }
}
