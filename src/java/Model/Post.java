package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Post {

    private int postId;
    private int userId;
    private String title;
    private String content;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private BigDecimal price;
    private BigDecimal area;
    private int statusId;
    private String statusName; // Để join với bảng post_status
    private String facebookPostId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Double distanceKm;
    
    // ✅ Scheduled posting fields
    private Timestamp scheduledAt;
    private boolean autoPublish;
    private Timestamp publishedAt;
    
    // User info (để join)
    private String userFullName;
    private String userPhone;
    private String userEmail;

    private String thumbnail;

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    // Constructors
    public Post() {
    }

    public Post(int postId, int userId, String title, String content, String address,
            BigDecimal lat, BigDecimal lng, BigDecimal price, BigDecimal area,
            int statusId, String statusName, String facebookPostId,
            Timestamp createdAt, Timestamp updatedAt) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.area = area;
        this.statusId = statusId;
        this.statusName = statusName;
        this.facebookPostId = facebookPostId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void getContent(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getFacebookPostId() {
        return facebookPostId;
    }

    public void setFacebookPostId(String facebookPostId) {
        this.facebookPostId = facebookPostId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // ✅ Scheduled posting getters and setters
    public Timestamp getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Timestamp scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public boolean isAutoPublish() {
        return autoPublish;
    }

    public void setAutoPublish(boolean autoPublish) {
        this.autoPublish = autoPublish;
    }

    public Timestamp getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Timestamp publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "Post{"
                + "postId=" + postId
                + ", userId=" + userId
                + ", title='" + title + '\''
                + ", address='" + address + '\''
                + ", price=" + price
                + ", area=" + area
                + ", statusName='" + statusName + '\''
                + ", createdAt=" + createdAt
                + ", scheduledAt=" + scheduledAt
                + ", autoPublish=" + autoPublish
                + '}';
    }
}
