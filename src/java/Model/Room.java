package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class Room {
    private int roomId;
    private int landlordId;
    
    // Thông tin bài đăng
    private String title;
    private String description;
    private String roomType; // PHONG_TRO, CHUNG_CU_MINI
    
    // Địa chỉ
    private int locationId;
    private String detailedAddress;
    
    // [THÊM MỚI] Tọa độ chính xác và thông tin địa chỉ chi tiết
    private Double latitude;
    private Double longitude;
    private String streetNumber;
    private String streetName;
    private String googlePlaceId;
    private String formattedAddress;
    private String neighborhood;
    private String postalCode;
    
    // [THÊM MỚI] Khoảng cách đến các địa điểm quan trọng (mét)
    private Integer distanceToUniversity;
    private Integer distanceToHospital;
    private Integer distanceToMarket;
    private Integer distanceToBusStop;
    private Integer distanceToMetro;
    
    // Thông tin phòng
    private BigDecimal area; // m2
    private BigDecimal monthlyRent;
    private BigDecimal depositAmount;
    
    // Tiện ích (đã sửa bớt theo database)
    private boolean hasAc;
    private boolean hasWifi;
    private boolean hasKitchen;
    private boolean hasFridge;
    private boolean hasWasher;
    
    // Thông tin liên hệ
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    
    // Trạng thái bài đăng
    private String postStatus; // PENDING, APPROVED, REJECTED, EXPIRED
    
    // Thông tin duyệt bài (ĐƠN GIẢN HÓA từ bảng riêng)
    private Integer reviewedBy;
    private Timestamp reviewedAt;
    private String rejectionReason;
    private String adminNotes;
    
    // Tính năng nổi bật
    private boolean isFeatured;
    private boolean isUrgent;
    
    // Facebook Auto Post (ĐƠN GIẢN HÓA)
    private boolean autoPostFacebook;
    private String facebookPostId;
    private Timestamp facebookPostedAt;
    
    // Analytics
    private int viewCount;
    private int contactCount;
    
    // Timestamps
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Join fields (từ các bảng khác)
    private String locationName;
    private String provinceName;
    private String districtName;
    private String wardName;
    private String landlordName;
    private String landlordPhone;
    private String thumbnailUrl;
    private int favoriteCount;
    
    // [THÊM MỚI] Các field cho tính năng map
    private double distanceKm; // Khoảng cách tính toán từ điểm tìm kiếm
    private String wardNameOnly; // Chỉ tên phường/xã
    
    // Constructors
    public Room() {}
    
    public Room(int roomId, String title, BigDecimal monthlyRent, String locationName) {
        this.roomId = roomId;
        this.title = title;
        this.monthlyRent = monthlyRent;
        this.locationName = locationName;
    }
    
    // Getters and Setters cũ (không thay đổi)
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public int getLandlordId() { return landlordId; }
    public void setLandlordId(int landlordId) { this.landlordId = landlordId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    
    public String getDetailedAddress() { return detailedAddress; }
    public void setDetailedAddress(String detailedAddress) { this.detailedAddress = detailedAddress; }
    
    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }
    
    public BigDecimal getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(BigDecimal monthlyRent) { this.monthlyRent = monthlyRent; }
    
    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }
    
    public boolean isHasAc() { return hasAc; }
    public void setHasAc(boolean hasAc) { this.hasAc = hasAc; }
    
    public boolean isHasWifi() { return hasWifi; }
    public void setHasWifi(boolean hasWifi) { this.hasWifi = hasWifi; }
    
    public boolean isHasKitchen() { return hasKitchen; }
    public void setHasKitchen(boolean hasKitchen) { this.hasKitchen = hasKitchen; }
    
    public boolean isHasFridge() { return hasFridge; }
    public void setHasFridge(boolean hasFridge) { this.hasFridge = hasFridge; }
    
    public boolean isHasWasher() { return hasWasher; }
    public void setHasWasher(boolean hasWasher) { this.hasWasher = hasWasher; }
    
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public String getPostStatus() { return postStatus; }
    public void setPostStatus(String postStatus) { this.postStatus = postStatus; }
    
    public Integer getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Integer reviewedBy) { this.reviewedBy = reviewedBy; }
    
    public Timestamp getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Timestamp reviewedAt) { this.reviewedAt = reviewedAt; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    
    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean isFeatured) { this.isFeatured = isFeatured; }
    
    public boolean isUrgent() { return isUrgent; }
    public void setUrgent(boolean isUrgent) { this.isUrgent = isUrgent; }
    
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    
    public int getContactCount() { return contactCount; }
    public void setContactCount(int contactCount) { this.contactCount = contactCount; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    // [THÊM MỚI] Getters/Setters cho tọa độ và địa chỉ chi tiết
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public String getStreetNumber() { return streetNumber; }
    public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
    
    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }
    
    public String getGooglePlaceId() { return googlePlaceId; }
    public void setGooglePlaceId(String googlePlaceId) { this.googlePlaceId = googlePlaceId; }
    
    public String getFormattedAddress() { return formattedAddress; }
    public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }
    
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    // [THÊM MỚI] Getters/Setters cho khoảng cách
    public Integer getDistanceToUniversity() { return distanceToUniversity; }
    public void setDistanceToUniversity(Integer distanceToUniversity) { this.distanceToUniversity = distanceToUniversity; }
    
    public Integer getDistanceToHospital() { return distanceToHospital; }
    public void setDistanceToHospital(Integer distanceToHospital) { this.distanceToHospital = distanceToHospital; }
    
    public Integer getDistanceToMarket() { return distanceToMarket; }
    public void setDistanceToMarket(Integer distanceToMarket) { this.distanceToMarket = distanceToMarket; }
    
    public Integer getDistanceToBusStop() { return distanceToBusStop; }
    public void setDistanceToBusStop(Integer distanceToBusStop) { this.distanceToBusStop = distanceToBusStop; }
    
    public Integer getDistanceToMetro() { return distanceToMetro; }
    public void setDistanceToMetro(Integer distanceToMetro) { this.distanceToMetro = distanceToMetro; }
    
    // [THÊM MỚI] Getters/Setters cho Facebook Auto Post
    public boolean isAutoPostFacebook() { return autoPostFacebook; }
    public void setAutoPostFacebook(boolean autoPostFacebook) { this.autoPostFacebook = autoPostFacebook; }
    
    public String getFacebookPostId() { return facebookPostId; }
    public void setFacebookPostId(String facebookPostId) { this.facebookPostId = facebookPostId; }
    
    public Timestamp getFacebookPostedAt() { return facebookPostedAt; }
    public void setFacebookPostedAt(Timestamp facebookPostedAt) { this.facebookPostedAt = facebookPostedAt; }
    
    // Join fields getters/setters
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    
    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
    
    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }
    
    public String getWardName() { return wardName; }
    public void setWardName(String wardName) { this.wardName = wardName; }
    
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    
    public String getLandlordPhone() { return landlordPhone; }
    public void setLandlordPhone(String landlordPhone) { this.landlordPhone = landlordPhone; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    
    // [THÊM MỚI] Getters/Setters cho tính năng map
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    
    public String getWardNameOnly() { return wardNameOnly; }
    public void setWardNameOnly(String wardNameOnly) { this.wardNameOnly = wardNameOnly; }
    
    // [CẬP NHẬT] Utility methods cho JSP
    public String getFormattedPrice() {
        if (monthlyRent == null) return "0";
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(monthlyRent);
    }
    
    public String getRoomTypeDisplay() {
        switch (roomType != null ? roomType : "") {
            case "PHONG_TRO": return "Phòng trọ";
            case "CHUNG_CU_MINI": return "Chung cư mini";
            default: return roomType;
        }
    }
    
    public String getPostStatusDisplay() {
        switch (postStatus != null ? postStatus : "") {
            case "PENDING": return "Chờ duyệt";
            case "APPROVED": return "Đã duyệt";
            case "REJECTED": return "Từ chối";
            case "EXPIRED": return "Hết hạn";
            default: return postStatus;
        }
    }
    
    public boolean isApproved() {
        return "APPROVED".equals(postStatus);
    }
    
    public boolean isPending() {
        return "PENDING".equals(postStatus);
    }
    
    public String getFullAddress() {
        return detailedAddress + (locationName != null ? ", " + locationName : "");
    }
    
    // Format area
    public String getFormattedArea() {
        if (area == null) return "N/A";
        return area + "m²";
    }
    
    // [THÊM MỚI] Các utility methods cho Google Maps
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
    public String getCoordinatesString() {
        if (!hasCoordinates()) return "";
        return latitude + "," + longitude;
    }
    
    public String getFormattedDistance() {
        if (distanceKm <= 0) return "";
        if (distanceKm < 1) {
            return String.format("%.0fm", distanceKm * 1000);
        } else {
            return String.format("%.1fkm", distanceKm);
        }
    }
    
    // [THÊM MỚI] Utility methods cho khoảng cách đến địa điểm
    public String getFormattedDistanceToUniversity() {
        return formatDistance(distanceToUniversity);
    }
    
    public String getFormattedDistanceToHospital() {
        return formatDistance(distanceToHospital);
    }
    
    public String getFormattedDistanceToMarket() {
        return formatDistance(distanceToMarket);
    }
    
    public String getFormattedDistanceToBusStop() {
        return formatDistance(distanceToBusStop);
    }
    
    public String getFormattedDistanceToMetro() {
        return formatDistance(distanceToMetro);
    }
    
    private String formatDistance(Integer distanceInMeters) {
        if (distanceInMeters == null || distanceInMeters <= 0) return "";
        
        if (distanceInMeters < 1000) {
            return distanceInMeters + "m";
        } else {
            double km = distanceInMeters / 1000.0;
            return String.format("%.1fkm", km);
        }
    }
    
    // [THÊM MỚI] Method để chuyển đổi cho JSON API
    public String toJsonString() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"roomId\":").append(roomId).append(",");
        json.append("\"title\":\"").append(escapeJson(title)).append("\",");
        json.append("\"monthlyRent\":").append(monthlyRent != null ? monthlyRent : 0).append(",");
        json.append("\"latitude\":").append(latitude != null ? latitude : "null").append(",");
        json.append("\"longitude\":").append(longitude != null ? longitude : "null").append(",");
        json.append("\"detailedAddress\":\"").append(escapeJson(detailedAddress)).append("\",");
        json.append("\"area\":").append(area != null ? area : 0).append(",");
        json.append("\"hasAc\":").append(hasAc).append(",");
        json.append("\"hasWifi\":").append(hasWifi).append(",");
        json.append("\"hasKitchen\":").append(hasKitchen).append(",");
        json.append("\"isFeatured\":").append(isFeatured).append(",");
        json.append("\"thumbnailUrl\":\"").append(escapeJson(thumbnailUrl)).append("\"");
        json.append("}");
        return json.toString();
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
    
    // [THÊM MỚI] Methods để set từ ResultSet trong DAO
    public void setArea(double area) {
        this.area = BigDecimal.valueOf(area);
    }
    
    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = BigDecimal.valueOf(monthlyRent);
    }
    
    public void setDepositAmount(double depositAmount) {
        this.depositAmount = BigDecimal.valueOf(depositAmount);
    }
    
    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", title='" + title + '\'' +
                ", roomType='" + roomType + '\'' +
                ", monthlyRent=" + monthlyRent +
                ", postStatus='" + postStatus + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distanceKm=" + distanceKm +
                '}';
    }
}