<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Controller.Admin.AdminController"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="Model.User"%>
<%@page import="Model.Post"%>
<%@ include file="/includes/csrf.jspf" %>

<%
    // Kiểm tra đăng nhập admin
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"ADMIN".equals(currentUser.getRoleName())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // Lấy dữ liệu posts và stats
    AdminController adminController = new AdminController();
    Map<String, Object> postsData = null;
    Map<String, Object> stats = null;
    
    try {
        postsData = adminController.getPostsData(1, 50, "all");
        stats = adminController.getDashboardStats();
    } catch (Exception e) {
        e.printStackTrace();
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý bài đăng - Admin PhongTro247</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    
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
            <a class="nav-link" href="/admin/dashboard">
                <i class="fas fa-tachometer-alt me-2"></i>Dashboard
            </a>
            <a class="nav-link active" href="/admin/posts">
                <i class="fas fa-newspaper me-2"></i>Quản lý bài đăng
            </a>
            <a class="nav-link" href="/admin/users">
                <i class="fas fa-users me-2"></i>Quản lý người dùng
            </a>
            <a class="nav-link" href="/admin/payments">
                <i class="fas fa-credit-card me-2"></i>Quản lý thanh toán
            </a>
            <hr class="text-white-50">
            <div class="px-3 py-2">
                <small class="text-white-50 d-block">Đăng nhập với:</small>
                <small class="text-white fw-bold"><%= currentUser.getFullName() %></small>
            </div>
            <a class="nav-link" href="/logout">
                <i class="fas fa-sign-out-alt me-2"></i>Đăng xuất
            </a>
        </nav>
    </div>

    <!-- Main Content -->
    <div class="admin-content">
        <!-- Dashboard Section -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2><i class="fas fa-newspaper me-2"></i>Quản lý bài đăng</h2>
            <span class="text-muted">Chào mừng, <%= currentUser.getFullName() %></span>
        </div>

        <!-- Stats Cards -->
        <div class="row">
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= stats != null ? stats.getOrDefault("totalPosts", 0) : 0 %></h3>
                            <p class="mb-0">Tổng bài đăng</p>
                        </div>
                        <i class="fas fa-newspaper fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card blue">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= stats != null ? stats.getOrDefault("pendingPosts", 0) : 0 %></h3>
                            <p class="mb-0">Chờ duyệt</p>
                        </div>
                        <i class="fas fa-clock fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card green">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= stats != null ? stats.getOrDefault("approvedPosts", 0) : 0 %></h3>
                            <p class="mb-0">Đã duyệt</p>
                        </div>
                        <i class="fas fa-check fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card orange">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= stats != null ? stats.getOrDefault("rejectedPosts", 0) : 0 %></h3>
                            <p class="mb-0">Đã từ chối</p>
                        </div>
                        <i class="fas fa-times fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Posts Table -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5><i class="fas fa-list me-2"></i>Danh sách bài đăng</h5>
                    </div>
                    <div class="card-body">
                <table class="table table-hover">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Tiêu đề</th>
                            <th>Tác giả</th>
                            <th>Giá</th>
                            <th>Trạng thái</th>
                            <th>Ngày tạo</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody id="postsTableBody">
                        <%
                            if (postsData != null && postsData.get("posts") != null) {
                                List<Post> posts = (List<Post>) postsData.get("posts");
                                for (Post post : posts) {
                        %>
                        <tr>
                            <td><%= post.getPostId() %></td>
                            <td>
                                <div class="fw-bold">
                                    <%= post.getTitle() != null ? post.getTitle() : "Không có tiêu đề" %>
                                </div>
                                <small class="text-muted">
                                    <%= post.getAddress() != null ? post.getAddress() : "Không có địa chỉ" %>
                                </small>
                            </td>
                            <td><%= post.getUserFullName() != null ? post.getUserFullName() : "N/A" %></td>
                            <td class="fw-bold text-success">
                                <% if (post.getPrice() != null && post.getPrice().compareTo(BigDecimal.ZERO) > 0) { %>
                                    <%= String.format("%,.0f VNĐ", post.getPrice()) %>
                                <% } else { %>
                                    Thỏa thuận
                                <% } %>
                            </td>
                            <td>
                                <% String status = post.getStatusName() != null ? post.getStatusName() : "PENDING"; %>
                                <% if ("PENDING".equals(status)) { %>
                                    <span class="badge bg-warning">Chờ duyệt</span>
                                <% } else if ("APPROVED".equals(status)) { %>
                                    <span class="badge bg-success">Đã duyệt</span>
                                <% } else if ("REJECTED".equals(status)) { %>
                                    <span class="badge bg-danger">Đã từ chối</span>
                                <% } %>
                            </td>
                            <td>
                                <% if (post.getCreatedAt() != null) { %>
                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(post.getCreatedAt()) %>
                                <% } else { %>
                                    N/A
                                <% } %>
                            </td>
                            <td>
                                <% if ("PENDING".equals(status)) { %>
                                    <form method="POST" action="<%= request.getContextPath() %>/admin/posts" style="display: inline;">
                                        <input type="hidden" name="action" value="updatePostStatus">
                                        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                                        <input type="hidden" name="status" value="approve">
                                        <input type="hidden" name="csrf-token" value="<%= session.getAttribute("csrf-token") %>">
                                        <button type="submit" class="btn btn-sm btn-success" title="Duyệt">
                                            <i class="fas fa-check"></i>
                                        </button>
                                    </form>
                                    <form method="POST" action="<%= request.getContextPath() %>/admin/posts" style="display: inline;">
                                        <input type="hidden" name="action" value="updatePostStatus">
                                        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                                        <input type="hidden" name="status" value="reject">
                                        <input type="hidden" name="csrf-token" value="<%= session.getAttribute("csrf-token") %>">
                                        <button type="submit" class="btn btn-sm btn-warning" title="Từ chối">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </form>
                                <% } %>
                                <form method="POST" action="<%= request.getContextPath() %>/admin/posts" style="display: inline;" 
                                      onsubmit="return confirm('Bạn có chắc muốn xóa bài đăng này?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                                    <input type="hidden" name="csrf-token" value="<%= session.getAttribute("csrf-token") %>">
                                    <button type="submit" class="btn btn-sm btn-danger" title="Xóa">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="7" class="text-center text-muted py-4">
                                Không có dữ liệu
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    
    <script>
        function filterPosts(status) {
            window.location.href = '<%= request.getContextPath() %>/admin/posts?status=' + status;
        }
    </script>
</body>
</html>
