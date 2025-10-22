    package Controller.Admin;

    import Model.User;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import jakarta.servlet.http.HttpSession;
    import java.io.IOException;

    @WebServlet(name = "AdminServlet", urlPatterns = {"/admin/dashboard", "/admin/posts", "/admin/users", "/admin/payments", "/admin/*"})
    public class AdminServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                throws ServletException, IOException {

            // Debug logging
            System.out.println("=== AdminServlet DEBUG ===");
            System.out.println("Request URI: " + request.getRequestURI());
            System.out.println("Context Path: " + request.getContextPath());
            System.out.println("Servlet Path: " + request.getServletPath());
            System.out.println("===========================");

            // Kiểm tra quyền admin
            if (!isAdmin(request, response)) {
                return;
            }

            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String path = requestURI.substring(contextPath.length());

            switch (path) {
                case "/admin/dashboard":
                case "/admin":
                case "/admin/":
                    // Redirect dashboard to users page
                    request.getRequestDispatcher("/admin_users.jsp").forward(request, response);
                    break;
                case "/admin/posts":
                    request.getRequestDispatcher("/admin_posts.jsp").forward(request, response);
                    break;
                case "/admin/users":
                    request.getRequestDispatcher("/admin_users.jsp").forward(request, response);
                    break;
                case "/admin/payments":
                    request.getRequestDispatcher("/admin_payments.jsp").forward(request, response);
                    break;
                default:
                    // Redirect unknown admin paths to dashboard
                    response.sendRedirect(contextPath + "/admin/dashboard");
                    break;
            }
        }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Kiểm tra quyền admin
        if (!isAdmin(request, response)) {
            return;
        }

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Xử lý actions cho users
        if (path.equals("/admin/users")) {
            handleUserActions(request, response);
            return;
        }
        
        // Xử lý actions cho posts  
        if (path.equals("/admin/posts")) {
            handlePostActions(request, response);
            return;
        }

        // Default: redirect to doGet
        doGet(request, response);
    }
    
    private void handleUserActions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userId");
        
        if (action == null || userIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            Controller.Admin.AdminController adminController = new Controller.Admin.AdminController();
            
            switch (action) {
                case "delete":
                    adminController.deleteUser(userId);
                    break;
                case "togglePro":
                    adminController.toggleUserProStatus(userId);
                    break;
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/users");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" + e.getMessage());
        }
    }
    
    private void handlePostActions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String postIdStr = request.getParameter("postId");
        String status = request.getParameter("status");
        
        if (action == null || postIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/posts");
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            Controller.Admin.AdminController adminController = new Controller.Admin.AdminController();
            
            if ("updatePostStatus".equals(action) && status != null) {
                String statusToUpdate = status.toUpperCase();
                if ("APPROVE".equals(statusToUpdate)) {
                    statusToUpdate = "APPROVED";
                } else if ("REJECT".equals(statusToUpdate)) {
                    statusToUpdate = "REJECTED";
                }
                adminController.updatePostStatus(postId, statusToUpdate);
            } else if ("delete".equals(action)) {
                adminController.deletePost(postId);
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/posts");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/posts?error=" + e.getMessage());
        }
    }
    
    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {

            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }

            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }

            if (!"ADMIN".equals(currentUser.getRoleName())) {
                // Nếu không phải admin, redirect về trang chủ
                response.sendRedirect(request.getContextPath() + "/");
                return false;
            }

            return true;
        }
    }