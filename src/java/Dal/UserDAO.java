package Dal;

import DBcontext.DBContext;
import Model.User;
import Utility.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Đăng nhập user
     * @param username
     * @param password
     * @return User object nếu thành công, null nếu thất bại
     */
    public User login(String username, String password) {
        String sql = "SELECT u.user_id, u.username, u.password, u.full_name, u.email, " +
                "u.phone, u.role_id, r.role_name, u.created_at, u.updated_at " +
                "FROM users u " +
                "INNER JOIN roles r ON u.role_id = r.role_id " +
                "WHERE u.username = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");

                    if (PasswordUtils.checkPassword(password, hashedPassword)) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setFullName(rs.getString("full_name"));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setRoleId(rs.getInt("role_id"));
                        user.setRoleName(rs.getString("role_name"));
                        user.setCreatedAt(rs.getTimestamp("created_at"));
                        user.setUpdatedAt(rs.getTimestamp("updated_at"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in login: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Thêm user mới
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // đã hash trước
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setInt(6, user.getRoleId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra email tồn tại
     */
    public boolean isEmailExist(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking email: " + e.getMessage());
        }

        return false;
    }

    /**
     * Kiểm tra username tồn tại
     */
    public boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }

        return false;
    }

    /**
     * Lấy user theo email
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT u.user_id, u.username, u.password, u.full_name, u.email, " +
                "u.phone, u.role_id, r.role_name, u.created_at, u.updated_at " +
                "FROM users u " +
                "INNER JOIN roles r ON u.role_id = r.role_id " +
                "WHERE u.email = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting user by email: " + e.getMessage());
        }

        return null;
    }

    /**
     * Cập nhật mật khẩu
     */
    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ?, updated_at = GETDATE() WHERE email = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy tất cả users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.password, u.full_name, u.email, " +
                "u.phone, u.role_id, r.role_name, u.created_at, u.updated_at " +
                "FROM users u " +
                "INNER JOIN roles r ON u.role_id = r.role_id " +
                "ORDER BY u.created_at DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setUpdatedAt(rs.getTimestamp("updated_at"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Xóa user theo ID
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy user theo username
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT u.user_id, u.username, u.password, u.full_name, u.email, " +
                "u.phone, u.role_id, r.role_name, u.created_at, u.updated_at " +
                "FROM users u " +
                "INNER JOIN roles r ON u.role_id = r.role_id " +
                "WHERE u.username = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password")); // hashed password
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting user by username: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
