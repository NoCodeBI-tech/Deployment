package io.nocodebi.model;

import java.sql.Timestamp;
import java.util.Date;

public class AccessToken {

    private String tokenId;
    private String userId;
    private String googleTokenId;
    private String googleAccessToken;
    private Timestamp issuedAt;
    private Date expiresAt;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGoogleAccessToken() {
        return googleAccessToken;
    }

    public void setGoogleAccessToken(String googleAccessToken) {
        this.googleAccessToken = googleAccessToken;
    }

    public String getGoogleTokenId() {
        return googleTokenId;
    }

    public void setGoogleTokenId(String googleTokenId) {
        this.googleTokenId = googleTokenId;
    }

    // Getters and setters
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Timestamp issuedAt) {
        this.issuedAt = issuedAt;
    }

    // Overloaded method to set issuedAt to the current time
    public void setIssuedAt() {
        this.issuedAt = new Timestamp(System.currentTimeMillis());
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isValid() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Check if access token has expired
        if (expiresAt != null && now.after(expiresAt)) {
            System.out.println("access token expired");
            return false;
        }

        return true;
    }

}
