package Facebook;

import Model.User;
import Model.FacebookSettings;
import Service.FacebookService;
import Dal.FacebookSettingsDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "FacebookManagementServlet", urlPatterns = {"/facebook/manage"})
public class FacebookManagementServlet extends HttpServlet {
    
    private final FacebookService facebookService = new FacebookService();
    private final FacebookSettingsDAO fbSettingsDAO = new FacebookSettingsDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        try {
            List<FacebookSettings> userPages = facebookService.getUserPages(user.getUserId());
            System.out.println("DEBUG: Found " + userPages.size() + " pages for user " + user.getUserId());
            for (FacebookSettings page : userPages) {
                System.out.println("DEBUG: Page - " + page.getPageName() + " (ID: " + page.getPageId() + ")");
            }
            req.setAttribute("facebookPages", userPages);
            req.setAttribute("pageCount", userPages.size());
            
        } catch (Exception e) {
            System.out.println("ERROR loading pages: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Lỗi tải danh sách Facebook Pages: " + e.getMessage());
        }
        
        req.getRequestDispatcher("/facebook_manage.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("DEBUG: No valid session, redirecting to login");
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");
        
        System.out.println("DEBUG: doPost called with action=" + action + " for user=" + user.getUserId());
        
        try {
            switch (action) {
                case "add":
                    System.out.println("DEBUG: Handling add action");
                    handleAddPage(req, user);
                    req.setAttribute("success", "Thêm Facebook Page thành công!");
                    break;
                case "update":
                    handleUpdatePage(req, user);
                    break;
                case "delete":
                    handleDeletePage(req, user);
                    break;
                case "toggle":
                    handleToggleAutoPost(req, user);
                    break;
                case "setDefault":
                    handleSetDefault(req, user);
                    break;
                default:
                    System.out.println("DEBUG: Unknown action: " + action);
                    req.setAttribute("error", "Hành động không hợp lệ");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in doPost: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Lỗi xử lý: " + e.getMessage());
        }
        
        System.out.println("DEBUG: Redirecting to /facebook/manage");
        resp.sendRedirect(req.getContextPath() + "/facebook/manage");
    }
    
    private void handleAddPage(HttpServletRequest req, User user) throws Exception {
        System.out.println("DEBUG: handleAddPage called for user " + user.getUserId());
        
        String pageId = req.getParameter("pageId");
        String pageName = req.getParameter("pageName");
        String accessToken = req.getParameter("accessToken");
        boolean autoPost = "on".equals(req.getParameter("autoPost"));
        boolean isDefault = "on".equals(req.getParameter("isDefault"));
        
        System.out.println("DEBUG: pageId=" + pageId);
        System.out.println("DEBUG: pageName=" + pageName);
        System.out.println("DEBUG: accessToken=" + (accessToken != null ? "***PROVIDED***" : "NULL"));
        System.out.println("DEBUG: autoPost=" + autoPost);
        System.out.println("DEBUG: isDefault=" + isDefault);
        
        if (pageId == null || pageId.trim().isEmpty() ||
            pageName == null || pageName.trim().isEmpty() ||
            accessToken == null || accessToken.trim().isEmpty()) {
            System.out.println("DEBUG: Validation failed - missing required fields");
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }
        
        FacebookSettings settings = new FacebookSettings();
        settings.setPageId(pageId.trim());
        settings.setPageName(pageName.trim());
        settings.setAccessToken(accessToken.trim());
        settings.setUserId(user.getUserId());
        settings.setActive(true);
        settings.setAutoPost(autoPost);
        settings.setDefault(isDefault);
        
        System.out.println("DEBUG: Calling fbSettingsDAO.saveOrUpdate...");
        fbSettingsDAO.saveOrUpdate(settings);
        System.out.println("DEBUG: saveOrUpdate completed successfully");
    }
    
    private void handleUpdatePage(HttpServletRequest req, User user) throws Exception {
        String pageId = req.getParameter("pageId");
        String pageName = req.getParameter("pageName");
        String accessToken = req.getParameter("accessToken");
        boolean autoPost = "on".equals(req.getParameter("autoPost"));
        
        FacebookSettings existing = fbSettingsDAO.getPageById(user.getUserId(), pageId);
        if (existing == null) {
            throw new IllegalArgumentException("Page không tồn tại");
        }
        
        existing.setPageName(pageName);
        existing.setAccessToken(accessToken);
        existing.setAutoPost(autoPost);
        
        fbSettingsDAO.update(existing);
    }
    
    private void handleDeletePage(HttpServletRequest req, User user) throws Exception {
        String pageId = req.getParameter("pageId");
        fbSettingsDAO.deletePage(user.getUserId(), pageId);
    }
    
    private void handleToggleAutoPost(HttpServletRequest req, User user) throws Exception {
        String pageId = req.getParameter("pageId");
        boolean autoPost = "true".equals(req.getParameter("autoPost"));
        fbSettingsDAO.toggleAutoPost(user.getUserId(), pageId, autoPost);
    }
    
    private void handleSetDefault(HttpServletRequest req, User user) throws Exception {
        String pageId = req.getParameter("pageId");
        fbSettingsDAO.setDefaultPage(user.getUserId(), pageId);
    }
}