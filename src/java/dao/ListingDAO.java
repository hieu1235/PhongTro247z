package dao;

import DBcontext.DBContext;
import models.Listing;
import models.ListingImage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListingDAO extends DBContext {
    
    /**
     * Lấy listing theo ID
     */
    public Listing getListingById(Long listingId) {
        String sql = "SELECT * FROM listings WHERE listing_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, listingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToListing(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getListingById: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy danh sách tin đăng cần auto post
     */
    public List<Listing> getPendingAutoPostListings() {
        List<Listing> listings = new ArrayList<>();
        String sql = """
            SELECT l.* FROM listings l 
            INNER JOIN users u ON l.landlord_id = u.user_id 
            WHERE l.status = 'approved' 
            AND l.auto_post_social = 1 
            AND u.facebook_page_access_token IS NOT NULL 
            AND u.facebook_page_id IS NOT NULL
            AND NOT EXISTS (
                SELECT 1 FROM activity_logs al 
                WHERE al.user_id = l.landlord_id 
                AND al.action_type = 'FACEBOOK_POST' 
                AND al.target_id = CAST(l.listing_id AS NVARCHAR(36))
                AND JSON_VALUE(al.details, '$.success') = 'true'
            )
            ORDER BY l.approved_at DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                listings.add(mapResultSetToListing(rs));
            }
            
        } catch (SQLException e) {
            System.out.println("Lỗi getPendingAutoPostListings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listings;
    }
    
    /**
     * Lấy danh sách listings theo landlord ID
     */
    public List<Listing> getListingsByLandlordId(Long landlordId) {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE landlord_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, landlordId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listings.add(mapResultSetToListing(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getListingsByLandlordId: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listings;
    }
    
    /**
     * Cập nhật auto post social setting
     */
    public boolean updateAutoPostSocial(Long listingId, boolean autoPostSocial) {
        String sql = "UPDATE listings SET auto_post_social = ?, updated_at = GETUTCDATE() WHERE listing_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, autoPostSocial);
            ps.setLong(2, listingId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Lỗi updateAutoPostSocial: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Approve listing
     */
    public boolean approveListing(Long listingId) {
        String sql = "UPDATE listings SET status = 'approved', approved_at = GETUTCDATE() WHERE listing_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, listingId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Lỗi approveListing: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy danh sách URL hình ảnh của tin đăng
     */
    public List<String> getListingImageUrls(Long listingId) {
        List<String> imageUrls = new ArrayList<>();
        String sql = "SELECT image_url FROM listing_images WHERE listing_id = ? ORDER BY is_thumbnail DESC, image_id ASC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, listingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imageUrls.add(rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getListingImageUrls: " + e.getMessage());
            e.printStackTrace();
        }
        
        return imageUrls;
    }
    
    /**
     * Lấy danh sách hình ảnh chi tiết
     */
    public List<ListingImage> getListingImages(Long listingId) {
        List<ListingImage> images = new ArrayList<>();
        String sql = "SELECT * FROM listing_images WHERE listing_id = ? ORDER BY is_thumbnail DESC, image_id ASC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, listingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ListingImage image = new ListingImage();
                    image.setImageId(rs.getLong("image_id"));
                    image.setListingId(rs.getLong("listing_id"));
                    image.setImageUrl(rs.getString("image_url"));
                    image.setThumbnail(rs.getBoolean("is_thumbnail"));
                    image.setUploadedAt(rs.getTimestamp("uploaded_at"));
                    
                    images.add(image);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getListingImages: " + e.getMessage());
            e.printStackTrace();
        }
        
        return images;
    }
    
    /**
     * Kiểm tra user có phải là chủ của listing không
     */
    public boolean isListingOwner(Long listingId, Long userId) {
        String sql = "SELECT COUNT(*) FROM listings WHERE listing_id = ? AND landlord_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, listingId);
            ps.setLong(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi isListingOwner: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy danh sách listing theo status
     */
    public List<Listing> getListingsByStatus(String status) {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE status = ? ORDER BY created_at DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listings.add(mapResultSetToListing(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getListingsByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listings;
    }
    
    /**
     * Map ResultSet thành Listing object
     */
    private Listing mapResultSetToListing(ResultSet rs) throws SQLException {
        Listing listing = new Listing();
        listing.setListingId(rs.getLong("listing_id"));
        listing.setLandlordId(rs.getLong("landlord_id"));
        listing.setTitle(rs.getString("title"));
        listing.setDescription(rs.getString("description"));
        listing.setAddressStreet(rs.getString("address_street"));
        listing.setAddressWard(rs.getString("address_ward"));
        listing.setAddressDistrict(rs.getString("address_district"));
        listing.setAddressCity(rs.getString("address_city"));
        listing.setLatitude(rs.getBigDecimal("latitude"));
        listing.setLongitude(rs.getBigDecimal("longitude"));
        listing.setPricePerMonth(rs.getBigDecimal("price_per_month"));
        listing.setAreaSqm(rs.getDouble("area_sqm"));
        listing.setMaxOccupants(rs.getInt("max_occupants"));
        listing.setStatus(rs.getString("status"));
        listing.setCreatedAt(rs.getTimestamp("created_at"));
        listing.setApprovedAt(rs.getTimestamp("approved_at"));
        listing.setExpiresAt(rs.getTimestamp("expires_at"));
        listing.setAutoPostSocial(rs.getBoolean("auto_post_social"));
        
        return listing;
    }
}