package models;

import java.sql.Timestamp;

public class ListingImage {
    private Long imageId;
    private Long listingId;
    private String imageUrl;
    private boolean isThumbnail;
    private Timestamp uploadedAt;
    
    // Constructors
    public ListingImage() {}
    
    public ListingImage(Long imageId, Long listingId, String imageUrl, 
                       boolean isThumbnail, Timestamp uploadedAt) {
        this.imageId = imageId;
        this.listingId = listingId;
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
        this.uploadedAt = uploadedAt;
    }
    
    // Getters and Setters
    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }
    
    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public boolean isThumbnail() { return isThumbnail; }
    public void setThumbnail(boolean thumbnail) { isThumbnail = thumbnail; }
    
    public Timestamp getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Timestamp uploadedAt) { this.uploadedAt = uploadedAt; }
    
    @Override
    public String toString() {
        return "ListingImage{" +
                "imageId=" + imageId +
                ", listingId=" + listingId +
                ", imageUrl='" + imageUrl + '\'' +
                ", isThumbnail=" + isThumbnail +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}