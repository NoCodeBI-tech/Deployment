package com.nocodebi.prodservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String userId;
    private String username;
    private String type; // "agency" | "client" | "developer"
    private Boolean subscribeEmailNotification;
    private Boolean agreement;
    private String proficiencyStatus;
    private String freelancingGoal;
    private String workPreference;
    private double hourlyRate;
    private Boolean emailVerification;
    private Boolean phoneNumberVerification;
    private String firstName;
    private String lastName;
    private String image;
    private String email;
    private String phoneNumber;
    private String country;
    private String dob;
    private String size;
    private String companyName;
    private String industryType;
    private String website;
    private String createdAt;
    private String updatedAt;
    private String authType;
    private Boolean onboard;
    private Boolean active;
    private StageUser stageUser;

    public void setDefault() {
        this.userId = "";
        this.username = "";
        this.type = "client"; // Default to "client"
        this.subscribeEmailNotification = true;
        this.agreement = false;
        this.proficiencyStatus = "beginner";
        this.freelancingGoal = "";
        this.workPreference = "";
        this.hourlyRate = 0.0;
        this.emailVerification = false;
        this.phoneNumberVerification = false;
        this.firstName = "";
        this.lastName = "";
        this.image = "";
        this.email = "";
        this.phoneNumber = "";
        this.country = "US"; // Default country
        this.dob = "";
        this.size = "small"; // Default company size
        this.companyName = "";
        this.industryType = "";
        this.website = "";
        this.createdAt = new Timestamp(System.currentTimeMillis()).toString();
        this.updatedAt = new Timestamp(System.currentTimeMillis()).toString();
        this.authType = "username"; // Default to "username" auth type
        this.onboard = false;
        this.active = true;
    }

    public StageUser getStageUser() {
        return stageUser;
    }

    public void setStageUser(StageUser stageUser) {
        this.stageUser = stageUser;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSubscribeEmailNotification() {
        return subscribeEmailNotification;
    }

    public void setSubscribeEmailNotification(Boolean subscribeEmailNotification) {
        this.subscribeEmailNotification = subscribeEmailNotification;
    }

    public Boolean getAgreement() {
        return agreement;
    }

    public void setAgreement(Boolean agreement) {
        this.agreement = agreement;
    }

    public String getProficiencyStatus() {
        return proficiencyStatus;
    }

    public void setProficiencyStatus(String proficiencyStatus) {
        this.proficiencyStatus = proficiencyStatus;
    }

    public String getFreelancingGoal() {
        return freelancingGoal;
    }

    public void setFreelancingGoal(String freelancingGoal) {
        this.freelancingGoal = freelancingGoal;
    }

    public String getWorkPreference() {
        return workPreference;
    }

    public void setWorkPreference(String workPreference) {
        this.workPreference = workPreference;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Boolean getEmailVerification() {
        return emailVerification;
    }

    public void setEmailVerification(Boolean emailVerification) {
        this.emailVerification = emailVerification;
    }

    public Boolean getPhoneNumberVerification() {
        return phoneNumberVerification;
    }

    public void setPhoneNumberVerification(Boolean phoneNumberVerification) {
        this.phoneNumberVerification = phoneNumberVerification;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Boolean getOnboard() {
        return onboard;
    }

    public void setOnboard(Boolean onboard) {
        this.onboard = onboard;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
