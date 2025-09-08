package utils;


import models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    
    public static final String USER_SESSION_KEY = "currentUser";
    public static final String USER_ID_SESSION_KEY = "userId";
    
    /**
     * Lưu user vào session
     */
    public static void setUserSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute(USER_SESSION_KEY, user);
        session.setAttribute(USER_ID_SESSION_KEY, user.getUserId());
    }
    
    /**
     * Lấy user từ session
     */
    public static User getUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute(USER_SESSION_KEY);
        }
        return null;
    }
    
    /**
     * Lấy user ID từ session
     */
    public static Long getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute(USER_ID_SESSION_KEY);
            if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }
    
    /**
     * Kiểm tra user đã đăng nhập chưa
     */
    public static boolean isUserLoggedIn(HttpServletRequest request) {
        return getUserFromSession(request) != null;
    }
    
    /**
     * Xóa session (logout)
     */
    public static void clearUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(USER_SESSION_KEY);
            session.removeAttribute(USER_ID_SESSION_KEY);
            session.invalidate();
        }
    }
}