package models;

import java.util.List;

public class FacebookPostRequest {
    private String message;
    private String link;
    private List<String> imageUrls;
    private String pageAccessToken;
    private String pageId;
    
    // Constructors
    public FacebookPostRequest() {}
    
    public FacebookPostRequest(String message, String link, List<String> imageUrls, 
                             String pageAccessToken, String pageId) {
        this.message = message;
        this.link = link;
        this.imageUrls = imageUrls;
        this.pageAccessToken = pageAccessToken;
        this.pageId = pageId;
    }
    
    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    
    public String getPageAccessToken() { return pageAccessToken; }
    public void setPageAccessToken(String pageAccessToken) { this.pageAccessToken = pageAccessToken; }
    
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }
}