package Model;

import java.sql.Timestamp;

public class FacebookSetting {
    private int settingId;
    private String pageId;
    private String pageName;
    private String accessToken;
    private int userId;
    private boolean isActive;
    private boolean autoPost;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // User info (để join)
    private String userFullName;
    
    // Constructors
    public FacebookSetting() {}
    
    public FacebookSetting(int settingId, String pageId, String pageName, 
                          String accessToken, int userId, boolean isActive, 
                          boolean autoPost, Timestamp createdAt, Timestamp updatedAt) {
        this.settingId = settingId;
        this.pageId = pageId;
        this.pageName = pageName;
        this.accessToken = accessToken;
        this.userId = userId;
        this.isActive = isActive;
        this.autoPost = autoPost;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getSettingId() { return settingId; }
    public void setSettingId(int settingId) { this.settingId = settingId; }
    
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
    
    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }
    
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public boolean isAutoPost() { return autoPost; }
    public void setAutoPost(boolean autoPost) { this.autoPost = autoPost; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    
    @Override
    public String toString() {
        return "FacebookSetting{" +
                "settingId=" + settingId +
                ", pageId='" + pageId + '\'' +
                ", pageName='" + pageName + '\'' +
                ", userId=" + userId +
                ", isActive=" + isActive +
                ", autoPost=" + autoPost +
                ", createdAt=" + createdAt +
                '}';
    }
}