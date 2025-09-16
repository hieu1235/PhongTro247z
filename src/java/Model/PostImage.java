package Model;

public class PostImage {
    private int imageId;
    private int postId;
    private String imageUrl;
    private boolean isThumbnail;
    
    // Constructors
    public PostImage() {}
    
    public PostImage(int imageId, int postId, String imageUrl, boolean isThumbnail) {
        this.imageId = imageId;
        this.postId = postId;
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
    }
    
    // Getters and Setters
    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }
    
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public boolean isThumbnail() { return isThumbnail; }
    public void setThumbnail(boolean thumbnail) { isThumbnail = thumbnail; }
    
    @Override
    public String toString() {
        return "PostImage{" +
                "imageId=" + imageId +
                ", postId=" + postId +
                ", imageUrl='" + imageUrl + '\'' +
                ", isThumbnail=" + isThumbnail +
                '}';
    }
}