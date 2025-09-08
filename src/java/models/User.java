package models;

import java.sql.Timestamp;

public class User {
    private Long userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String facebookPageAccessToken;
    private String facebookPageId;
    
    // Constructors
    public User() {}
    
    public User(Long userId, String username, String passwordHash, String fullName, 
                String email, String phoneNumber, String avatarUrl, String status,
                Timestamp createdAt, Timestamp updatedAt, String facebookPageAccessToken, 
                String facebookPageId) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.facebookPageAccessToken = facebookPageAccessToken;
        this.facebookPageId = facebookPageId;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getFacebookPageAccessToken() { return facebookPageAccessToken; }
    public void setFacebookPageAccessToken(String facebookPageAccessToken) { 
        this.facebookPageAccessToken = facebookPageAccessToken; 
    }
    
    public String getFacebookPageId() { return facebookPageId; }
    public void setFacebookPageId(String facebookPageId) { this.facebookPageId = facebookPageId; }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status='" + status + '\'' +
                ", facebookPageId='" + facebookPageId + '\'' +
                '}';
    }
}