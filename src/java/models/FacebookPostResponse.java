package models;

public class FacebookPostResponse {
    private boolean success;
    private String postId;
    private String message;
    private String errorCode;
    private String errorDetails;
    
    // Constructors
    public FacebookPostResponse() {}
    
    public FacebookPostResponse(boolean success, String postId, String message) {
        this.success = success;
        this.postId = postId;
        this.message = message;
    }
    
    public FacebookPostResponse(boolean success, String message, String errorCode, String errorDetails) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorDetails() { return errorDetails; }
    public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }
}