<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@page import="Model.User"%>
<%@page import="Controller.Admin.AdminController"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.math.BigDecimal"%>

<%
    // Kiểm tra đăng nhập admin
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"ADMIN".equals(currentUser.getRoleName())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // Load dữ liệu từ AdminController
    AdminController adminController = new AdminController();
    Map<String, Object> paymentStats = null;
    Map<String, Object> paymentsData = null;
    
    try {
        paymentStats = adminController.getPaymentStats();
        paymentsData = adminController.getPaymentsData(1, 100);
    } catch (Exception e) {
        e.printStackTrace();
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý thanh toán - Admin PhongTro247</title>
    
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
            <a class="nav-link" href="/admin/dashboard">
                <i class="fas fa-tachometer-alt me-2"></i>Dashboard
            </a>
            <a class="nav-link" href="/admin/posts">
                <i class="fas fa-newspaper me-2"></i>Quản lý bài đăng
            </a>
            <a class="nav-link" href="/admin/users">
                <i class="fas fa-users me-2"></i>Quản lý người dùng
            </a>
            <a class="nav-link active" href="/admin/payments">
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
            <h2><i class="fas fa-credit-card me-2"></i>Quản lý thanh toán</h2>
            <div>
                <button class="btn btn-sm btn-secondary me-2" onclick="refreshPaymentStats()">
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
                            <h3><%= paymentStats != null ? paymentStats.getOrDefault("totalPayments", 0) : 0 %></h3>
                            <p class="mb-0">Tổng giao dịch</p>
                        </div>
                        <i class="fas fa-credit-card fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card blue">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= paymentStats != null ? paymentStats.getOrDefault("successPayments", 0) : 0 %></h3>
                            <p class="mb-0">Thành công</p>
                        </div>
                        <i class="fas fa-check-circle fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card green">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h3><%= paymentStats != null ? paymentStats.getOrDefault("pendingPayments", 0) : 0 %></h3>
                            <p class="mb-0">Đang xử lý</p>
                        </div>
                        <i class="fas fa-clock fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card orange">
                    <div class="d-flex justify-content-between">
                        <div>
                            <%
                                BigDecimal revenue = paymentStats != null ? (BigDecimal) paymentStats.getOrDefault("totalRevenue", BigDecimal.ZERO) : BigDecimal.ZERO;
                                String formattedRevenue = String.format("%,.0f VNĐ", revenue);
                            %>
                            <h3><%= formattedRevenue %></h3>
                            <p class="mb-0">Tổng doanh thu</p>
                        </div>
                        <i class="fas fa-money-bill-wave fa-2x opacity-50"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Payments Table -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5><i class="fas fa-list me-2"></i>Danh sách giao dịch</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover" id="paymentsTable">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Mã đơn</th>
                                        <th>Người dùng</th>
                                        <th>Số tiền</th>
                                        <th>Phương thức</th>
                                        <th>Trạng thái</th>
                                        <th>Ngày tạo</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        if (paymentsData != null && paymentsData.get("payments") != null) {
                                            @SuppressWarnings("unchecked")
                                            List<Map<String, Object>> payments = (List<Map<String, Object>>) paymentsData.get("payments");
                                            for (Map<String, Object> payment : payments) {
                                                String status = (String) payment.get("status");
                                                String statusClass = "secondary";
                                                if ("success".equals(status)) statusClass = "success";
                                                else if ("pending".equals(status)) statusClass = "warning";
                                                else if ("failed".equals(status)) statusClass = "danger";
                                    %>
                                    <tr>
                                        <td><%= payment.get("orderId") %></td>
                                        <td><%= payment.get("orderCode") %></td>
                                        <td><%= payment.get("userName") != null ? payment.get("userName") : "N/A" %></td>
                                        <td class="fw-bold">
                                            <%
                                                BigDecimal amount = (BigDecimal) payment.get("amount");
                                                String formattedAmount = String.format("%,.0f ₫", amount);
                                            %>
                                            <%= formattedAmount %>
                                        </td>
                                        <td>
                                            <span class="badge bg-info"><%= ((String) payment.get("paymentMethod")).toUpperCase() %></span>
                                        </td>
                                        <td>
                                            <span class="badge bg-<%= statusClass %>"><%= status.toUpperCase() %></span>
                                        </td>
                                        <td>
                                            <% if (payment.get("createdAt") != null) { %>
                                                <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(payment.get("createdAt")) %>
                                            <% } else { %>
                                                N/A
                                            <% } %>
                                        </td>
                                        <td>
                                            <button class="btn btn-sm btn-info" disabled title="Xem chi tiết">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                        </td>
                                    </tr>
                                    <%
                                            }
                                        } else {
                                    %>
                                    <tr>
                                        <td colspan="8" class="text-center text-muted py-4">Không có dữ liệu</td>
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
        function refreshPaymentStats() {
            window.location.reload();
        }
    </script>
</body>
</html>
