package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Listing {
    private Long listingId;
    private Long landlordId;
    private String title;
    private String description;
    private String addressStreet;
    private String addressWard;
    private String addressDistrict;
    private String addressCity;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal pricePerMonth;
    private Double areaSqm;
    private Integer maxOccupants;
    private String status;
    private Timestamp createdAt;
    private Timestamp approvedAt;
    private Timestamp expiresAt;
    private boolean autoPostSocial;
    
    // Constructors
    public Listing() {}
    
    public Listing(Long listingId, Long landlordId, String title, String description,
                   String addressStreet, String addressWard, String addressDistrict, 
                   String addressCity, BigDecimal latitude, BigDecimal longitude,
                   BigDecimal pricePerMonth, Double areaSqm, Integer maxOccupants,
                   String status, Timestamp createdAt, Timestamp approvedAt,
                   Timestamp expiresAt, boolean autoPostSocial) {
        this.listingId = listingId;
        this.landlordId = landlordId;
        this.title = title;
        this.description = description;
        this.addressStreet = addressStreet;
        this.addressWard = addressWard;
        this.addressDistrict = addressDistrict;
        this.addressCity = addressCity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pricePerMonth = pricePerMonth;
        this.areaSqm = areaSqm;
        this.maxOccupants = maxOccupants;
        this.status = status;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.expiresAt = expiresAt;
        this.autoPostSocial = autoPostSocial;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public Long getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(Long landlordId) {
        this.landlordId = landlordId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressWard() {
        return addressWard;
    }

    public void setAddressWard(String addressWard) {
        this.addressWard = addressWard;
    }

    public String getAddressDistrict() {
        return addressDistrict;
    }

    public void setAddressDistrict(String addressDistrict) {
        this.addressDistrict = addressDistrict;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(BigDecimal pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    public Double getAreaSqm() {
        return areaSqm;
    }

    public void setAreaSqm(Double areaSqm) {
        this.areaSqm = areaSqm;
    }

    public Integer getMaxOccupants() {
        return maxOccupants;
    }

    public void setMaxOccupants(Integer maxOccupants) {
        this.maxOccupants = maxOccupants;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isAutoPostSocial() {
        return autoPostSocial;
    }

    public void setAutoPostSocial(boolean autoPostSocial) {
        this.autoPostSocial = autoPostSocial;
    }
    
   
    // Utility method để lấy địa chỉ đầy đủ
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        
        if (addressStreet != null && !addressStreet.trim().isEmpty()) {
            address.append(addressStreet).append(", ");
        }
        if (addressWard != null && !addressWard.trim().isEmpty()) {
            address.append(addressWard).append(", ");
        }
        if (addressDistrict != null && !addressDistrict.trim().isEmpty()) {
            address.append(addressDistrict).append(", ");
        }
        if (addressCity != null && !addressCity.trim().isEmpty()) {
            address.append(addressCity);
        }
        
        return address.toString().replaceAll(", $", "");
    }
    
    @Override
    public String toString() {
        return "Listing{" +
                "listingId=" + listingId +
                ", landlordId=" + landlordId +
                ", title='" + title + '\'' +
                ", addressDistrict='" + addressDistrict + '\'' +
                ", addressCity='" + addressCity + '\'' +
                ", pricePerMonth=" + pricePerMonth +
                ", areaSqm=" + areaSqm +
                ", status='" + status + '\'' +
                ", autoPostSocial=" + autoPostSocial +
                '}';
    }
}