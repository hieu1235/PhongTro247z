package Model;

import java.sql.Timestamp;

/**
 * Model cho Facebook Page settings
 */
public class FacebookSettings {
    private int settingId;
    private String pageId;
    private String pageName;
    private String accessToken;
    private int userId;
    private boolean isActive;
    private boolean autoPost;
    private boolean isDefault;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public FacebookSettings() {}

    public FacebookSettings(String pageId, String pageName, String accessToken, int userId) {
        this.pageId = pageId;
        this.pageName = pageName;
        this.accessToken = accessToken;
        this.userId = userId;
        this.isActive = true;
        this.autoPost = true;
        this.isDefault = false;
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

    public boolean isDefault() { return isDefault; }
    public boolean getDefault() { return isDefault; } // Để JSP có thể access qua ${page['default']}
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "FacebookSettings{" +
                "settingId=" + settingId +
                ", pageId='" + pageId + '\'' +
                ", pageName='" + pageName + '\'' +
                ", userId=" + userId +
                ", isActive=" + isActive +
                ", autoPost=" + autoPost +
                ", isDefault=" + isDefault +
                '}';
    }
}