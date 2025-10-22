package Model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private int roleId;
    private String roleName; // Để lưu tên role từ join query
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Verification fields
    private boolean isVerified = false;
    private String verificationCode;
    private String resetToken;
    private Timestamp resetTokenExpires;
    
    // Coin system fields
    private int coins = 0;
    
    // Pro system fields
    private boolean isPro = false;
    private Timestamp proExpiresAt;
    
    // Constructors
    public User() {}
    
    public User(String username, String password, String fullName, String email, 
                String phone, int roleId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public int getRoleId() {
        return roleId;
    }
    
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
    public String getResetToken() {
        return resetToken;
    }
    
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    
    public Timestamp getResetTokenExpires() {
        return resetTokenExpires;
    }
    
    public void setResetTokenExpires(Timestamp resetTokenExpires) {
        this.resetTokenExpires = resetTokenExpires;
    }
    
    public int getCoins() {
        return coins;
    }
    
    public void setCoins(int coins) {
        this.coins = coins;
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
    
    // Pro system getters and setters
    public boolean isPro() {
        return isPro;
    }
    
    public void setPro(boolean isPro) {
        this.isPro = isPro;
    }
    
    /**
     * Alias method for JSP compatibility
     */
    public boolean getIsPro() {
        return isPro;
    }
    
    public Timestamp getProExpiresAt() {
        return proExpiresAt;
    }
    
    public void setProExpiresAt(Timestamp proExpiresAt) {
        this.proExpiresAt = proExpiresAt;
    }
    
    /**
     * Kiểm tra Pro status có còn hiệu lực không
     */
    public boolean isProActive() {
        if (!isPro || proExpiresAt == null) {
            return false;
        }
        return proExpiresAt.after(new Timestamp(System.currentTimeMillis()));
    }
    
    /**
     * Lấy số ngày còn lại của gói Pro
     */
    public long getProDaysRemaining() {
        if (!isProActive()) {
            return 0;
        }
        long diffMs = proExpiresAt.getTime() - System.currentTimeMillis();
        return Math.max(0, diffMs / (24 * 60 * 60 * 1000));
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}