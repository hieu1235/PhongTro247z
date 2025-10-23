<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://jakarta.ee/tags/core" %>
<%@taglib prefix="fmt" uri="http://jakarta.ee/tags/fmt" %>
<%@page import="Model.User"%>
<%@page import="Controller.Admin.AdminController"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
    // Kiểm tra đăng nhập admin
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"ADMIN".equals(currentUser.getRoleName())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // Load data từ AdminController
    AdminController adminController = new AdminController();
    Map<String, Object> stats = null;
    Map<String, Object> usersData = null;
    
    try {
        stats = adminController.getDashboardStats();
        usersData = adminController.getUsersData(1, 100, "all");
    } catch (Exception e) {
        e.printStackTrace();
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý người dùng - Admin PhongTro247</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <%@ include file="/includes/csrf.jspf" %>
    
    <style>
        .admin-sidebar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            width: 250px;
            z-index: 1000;
        }
        .admin-content {
            margin-left: 250px;
            padding: 20px;
        }
        .nav-link {
            color: white !important;
            border-radius: 5px;
            margin: 2px 10px;
        }
        .nav-link:hover, .nav-link.active {
            background-color: rgba(255,255,255,0.2);
        }
        .stat-card {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .stat-card.blue {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .stat-card.green {
            background: linear-gradient(135deg, #56ccf2 0%, #2f80ed 100%);
        }
        .stat-card.orange {
            background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
            color: #333;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div class="admin-sidebar">
        <div class="text-center py-4">
            <h5 class="text-white mb-0">
                <i class="fas fa-crown me-2"></i>Admin Panel
            </h5>
            <small class="text-white-50">PhongTro247</small>
        </div>

        <nav class="nav flex-column px-2">
            <a class="nav-link" href="/PhongTroNew/admin/dashboard">
                <i class="fas fa-tachometer-alt me-2"></i>Dashboard
            </a>
            <a class="nav-link" href="/PhongTroNew/admin/posts">
                <i class="fas fa-newspaper me-2"></i>Quản lý bài đăng
            </a>
            <a class="nav-link active" href="/PhongTroNew/admin/users">
                <i class="fas fa-users me-2"></i>Quản lý người dùng
            </a>
            <a class="nav-link" href="/PhongTroNew/admin/payments">
                <i class="fas fa-credit-card me-2"></i>Quản lý thanh toán
            </a>
            <hr class="text-white-50">
            <div class="px-3 py-2">
                <small class="text-white-50 d-block">Đăng nhập với:</small>
                <small class="text-white fw-bold"><%= currentUser.getFullName() %></small>
            </div>
            <a class="nav-link" href="/PhongTroNew/logout">
                <i class="fas fa-sign-out-alt me-2"></i>Đăng xuất
            </a>
        </nav>
    </div>

    <!-- Main Content -->
    <div class="admin-content">
        <!-- Dashboard Section -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2><i class="fas fa-users me-2"></i>Quản lý người dùng</h2>
            <div>
                <button class="btn btn-sm btn-secondary me-2" onclick="refreshStats()">
                    <i class="fas fa-sync-alt"></i> Refresh
                </button>
                <span class="text-muted">Chào mừng, <%= currentUser.getFullName() %></span>
            </div>
        </div>

        <!-- Stats Cards -->
        <div class="row">
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= stats != null ? stats.getOrDefault("totalUsers", 0) : 0 %></h3>
                            <p class="mb-0">Tổng người dùng</p>
                        </div>
                        <i class="fas fa-users fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card blue">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= stats != null ? stats.getOrDefault("proUsers", 0) : 0 %></h3>
                            <p class="mb-0">Người dùng Pro</p>
                        </div>
                        <i class="fas fa-crown fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card green">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= stats != null ? stats.getOrDefault("verifiedUsers", 0) : 0 %></h3>
                            <p class="mb-0">Đã xác thực</p>
                        </div>
                        <i class="fas fa-user-check fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card orange">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= stats != null ? stats.getOrDefault("totalCoins", 0) : 0 %></h3>
                            <p class="mb-0">Tổng xu</p>
                        </div>
                        <i class="fas fa-coins fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Users Table -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5><i class="fas fa-list me-2"></i>Danh sách người dùng</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover" id="usersTable">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Tên đăng nhập</th>
                                        <th>Họ tên</th>
                                        <th>Email</th>
                                        <th>Vai trò</th>
                                        <th>Pro</th>
                                        <th>Xu</th>
                                        <th>Ngày tạo</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        if (usersData != null && usersData.get("users") != null) {
                                            @SuppressWarnings("unchecked")
                                            List<User> users = (List<User>) usersData.get("users");
                                            for (User user : users) {
                                                String roleBadgeClass = "secondary";
                                                if ("ADMIN".equals(user.getRoleName())) roleBadgeClass = "danger";
                                                else if ("USER".equals(user.getRoleName())) roleBadgeClass = "primary";
                                                else if ("MODERATOR".equals(user.getRoleName())) roleBadgeClass = "info";
                                    %>
                                    <tr>
                                        <td><%= user.getUserId() %></td>
                                        <td><%= user.getUsername() %></td>
                                        <td><%= user.getFullName() %></td>
                                        <td><%= user.getEmail() %></td>
                                        <td><span class="badge bg-<%= roleBadgeClass %>"><%= user.getRoleName() %></span></td>
                                        <td>
                                            <% if (user.isPro()) { %>
                                                <span class="badge bg-warning">PRO</span>
                                            <% } else { %>
                                                <span class="badge bg-light text-dark">FREE</span>
                                            <% } %>
                                        </td>
                                        <td><span class="badge bg-success"><%= user.getCoins() %> xu</span></td>
                                        <td>
                                            <% if (user.getCreatedAt() != null) { %>
                                                <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(user.getCreatedAt()) %>
                                            <% } else { %>
                                                N/A
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (!"ADMIN".equals(user.getRoleName())) { %>
                                                <form method="POST" action="<%= request.getContextPath() %>/admin/users" style="display: inline;">
                                                    <input type="hidden" name="action" value="togglePro">
                                                    <input type="hidden" name="userId" value="<%= user.getUserId() %>">
                                                    <input type="hidden" name="csrf-token" value="<%= session.getAttribute("csrf-token") %>">
                                                    <button type="submit" class="btn btn-sm btn-warning" title="Toggle Pro">
                                                        <i class="fas fa-crown"></i>
                                                    </button>
                                                </form>
                                                <form method="POST" action="<%= request.getContextPath() %>/admin/users" style="display: inline;" 
                                                      onsubmit="return confirm('Bạn có chắc muốn xóa người dùng này?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="userId" value="<%= user.getUserId() %>">
                                                    <input type="hidden" name="csrf-token" value="<%= session.getAttribute("csrf-token") %>">
                                                    <button type="submit" class="btn btn-sm btn-danger" title="Xóa">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            <% } %>
                                        </td>
                                    </tr>
                                    <%
                                            }
                                        } else {
                                    %>
                                    <tr>
                                        <td colspan="9" class="text-center text-muted py-4">Không có dữ liệu</td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        function refreshStats() {
            window.location.reload();
        }
    </script>
</body>
</html>
