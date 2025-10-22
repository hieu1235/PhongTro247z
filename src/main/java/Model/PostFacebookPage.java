package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model cho bảng post_facebook_pages - quản lý relationship giữa posts và Facebook pages
 */
public class PostFacebookPage {
    private int id;
    private int postId;
    private String pageId;
    private String facebookPostId;
    private Timestamp postedAt;
    private String status; // PENDING, SUCCESS, FAILED
    private String errorMessage;
    
    // Join fields từ bảng posts (cho việc hiển thị)
    private String postTitle;
    private String postContent;
    private String postAddress;
    private BigDecimal postPrice;
    private BigDecimal postArea;
    
    // Join fields từ bảng facebook_settings
    private String pageName;
    
    // Constructors
    public PostFacebookPage() {}
    
    public PostFacebookPage(int postId, String pageId) {
        this.postId = postId;
        this.pageId = pageId;
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPostId() {
        return postId;
    }
    
    public void setPostId(int postId) {
        this.postId = postId;
    }
    
    public String getPageId() {
        return pageId;
    }
    
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }
    
    public String getFacebookPostId() {
        return facebookPostId;
    }
    
    public void setFacebookPostId(String facebookPostId) {
        this.facebookPostId = facebookPostId;
    }
    
    public Timestamp getPostedAt() {
        return postedAt;
    }
    
    public void setPostedAt(Timestamp postedAt) {
        this.postedAt = postedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    // Join fields getters/setters
    public String getPostTitle() {
        return postTitle;
    }
    
    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
    
    public String getPostContent() {
        return postContent;
    }
    
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
    
    public String getPostAddress() {
        return postAddress;
    }
    
    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }
    
    public BigDecimal getPostPrice() {
        return postPrice;
    }
    
    public void setPostPrice(BigDecimal postPrice) {
        this.postPrice = postPrice;
    }
    
    public BigDecimal getPostArea() {
        return postArea;
    }
    
    public void setPostArea(BigDecimal postArea) {
        this.postArea = postArea;
    }
    
    public String getPageName() {
        return pageName;
    }
    
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
    
    // Helper methods
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    @Override
    public String toString() {
        return "PostFacebookPage{" +
                "id=" + id +
                ", postId=" + postId +
                ", pageId='" + pageId + '\'' +
                ", facebookPostId='" + facebookPostId + '\'' +
                ", status='" + status + '\'' +
                ", postedAt=" + postedAt +
                '}';
    }
}