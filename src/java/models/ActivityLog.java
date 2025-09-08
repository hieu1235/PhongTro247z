package models;

import java.sql.Timestamp;

public class ActivityLog {
    private Long logId;
    private Long userId;
    private String actionType;
    private String targetId;
    private String targetType;
    private String summary;
    private String details;
    private Timestamp createdAt;
    
    // Constructors
    public ActivityLog() {}
    
    public ActivityLog(Long logId, Long userId, String actionType, String targetId,
                      String targetType, String summary, String details, Timestamp createdAt) {
        this.logId = logId;
        this.userId = userId;
        this.actionType = actionType;
        this.targetId = targetId;
        this.targetType = targetType;
        this.summary = summary;
        this.details = details;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "ActivityLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", actionType='" + actionType + '\'' +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", summary='" + summary + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}