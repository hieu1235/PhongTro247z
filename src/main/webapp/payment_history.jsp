<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://jakarta.ee/tags/core" %>
<%@taglib prefix="fmt" uri="http://jakarta.ee/tags/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch Sử Thanh Toán - PhongTro247</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <style>
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 60px 0;
            margin-bottom: 30px;
        }
        .stats-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            transition: transform 0.3s;
        }
        .stats-card:hover {
            transform: translateY(-5px);
        }
        .stats-number {
            font-size: 2.5rem;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .payment-table {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .status-badge {
            padding: 8px 15px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            text-transform: uppercase;
        }
        .status-success {
            background: #d4edda;
            color: #155724;
        }
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        .status-failed {
            background: #f8d7da;
            color: #721c24;
        }
        .status-expired {
            background: #e2e3e5;
            color: #383d41;
        }
        .payment-method {
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }
        .method-icon {
            width: 30px;
            height: 30px;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 14px;
        }
        .method-momo { background: #d82d8b; }
        .method-zalopay { background: #0068ff; }
        .method-vnpay { background: #1f4788; }
        .method-banking { background: #28a745; }
        .filter-section {
            background: #f8f9fa;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 30px;
        }
        .btn-filter {
            background: #6c757d;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 8px;
            margin: 5px;
            transition: all 0.3s;
        }
        .btn-filter:hover, .btn-filter.active {
            background: #495057;
            transform: translateY(-2px);
        }
        .order-detail-modal .modal-content {
            border-radius: 15px;
        }
        .order-detail-header {
            background: linear-gradient(45deg, #FF6B6B, #4ECDC4);
            color: white;
            border-radius: 15px 15px 0 0;
        }
        .detail-row {
            padding: 12px 0;
            border-bottom: 1px solid #eee;
        }
        .detail-row:last-child {
            border-bottom: none;
        }
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        .empty-state i {
            font-size: 64px;
            margin-bottom: 20px;
            opacity: 0.5;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <div class="page-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1><i class="fas fa-history"></i> Lịch Sử Thanh Toán</h1>
                    <p class="lead mb-0">Theo dõi tất cả giao dịch nạp xu của bạn</p>
                </div>
                <div class="col-md-4 text-end">
                    <a href="subscription" class="btn btn-light btn-lg">
                        <i class="fas fa-plus"></i> Nạp Xu Mới
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="stats-card text-center">
                    <div class="stats-number text-primary">${totalOrders}</div>
                    <div class="text-muted">Tổng đơn hàng</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card text-center">
                    <div class="stats-number text-success">
                        <fmt:formatNumber value="${totalSuccessAmount}" pattern="#,###"/>
                    </div>
                    <div class="text-muted">Tổng tiền đã nạp (VNĐ)</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card text-center">
                    <div class="stats-number text-warning">
                        <fmt:formatNumber value="${totalCoins}" pattern="#,###"/>
                    </div>
                    <div class="text-muted">Tổng xu đã nhận</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card text-center">
                    <div class="stats-number text-info">${successRate}%</div>
                    <div class="text-muted">Tỷ lệ thành công</div>
                </div>
            </div>
        </div>

        <!-- Filter Section -->
        <div class="filter-section">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h5 class="mb-3"><i class="fas fa-filter"></i> Bộ Lọc</h5>
                    <div class="d-flex flex-wrap">
                        <button class="btn-filter active" onclick="filterByStatus('all')" data-status="all">
                            Tất cả
                        </button>
                        <button class="btn-filter" onclick="filterByStatus('SUCCESS')" data-status="SUCCESS">
                            <i class="fas fa-check"></i> Thành công
                        </button>
                        <button class="btn-filter" onclick="filterByStatus('PENDING')" data-status="PENDING">
                            <i class="fas fa-clock"></i> Chờ thanh toán
                        </button>
                        <button class="btn-filter" onclick="filterByStatus('FAILED')" data-status="FAILED">
                            <i class="fas fa-times"></i> Thất bại
                        </button>
                        <button class="btn-filter" onclick="filterByStatus('EXPIRED')" data-status="EXPIRED">
                            <i class="fas fa-hourglass-end"></i> Hết hạn
                        </button>
                    </div>
                </div>
                <div class="col-md-4 text-end">
                    <div class="input-group">
                        <input type="text" id="searchInput" class="form-control" placeholder="Tìm kiếm mã đơn hàng...">
                        <button class="btn btn-outline-secondary" type="button">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Payment Table -->
        <div class="payment-table">
            <c:choose>
                <c:when test="${empty paymentOrders}">
                    <div class="empty-state">
                        <i class="fas fa-receipt"></i>
                        <h4>Chưa có giao dịch nào</h4>
                        <p>Bạn chưa thực hiện giao dịch nạp xu nào. Hãy nạp xu để trải nghiệm các tính năng Pro!</p>
                        <a href="subscription" class="btn btn-primary btn-lg">
                            <i class="fas fa-plus"></i> Nạp Xu Ngay
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table id="paymentTable" class="table table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>Mã đơn hàng</th>
                                    <th>Phương thức</th>
                                    <th>Số tiền</th>
                                    <th>Xu nhận</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày tạo</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${paymentOrders}">
                                    <tr data-status="${order.status}">
                                        <td>
                                            <strong>${order.orderCode}</strong>
                                            <c:if test="${not empty order.description}">
                                                <br><small class="text-muted">${order.description}</small>
                                            </c:if>
                                        </td>
                                        <td>
                                            <div class="payment-method">
                                                <c:choose>
                                                    <c:when test="${order.paymentMethod eq 'momo'}">
                                                        <div class="method-icon method-momo">
                                                            <i class="fas fa-mobile-alt"></i>
                                                        </div>
                                                        MoMo
                                                    </c:when>
                                                    <c:when test="${order.paymentMethod eq 'zalopay'}">
                                                        <div class="method-icon method-zalopay">
                                                            <i class="fas fa-wallet"></i>
                                                        </div>
                                                        ZaloPay
                                                    </c:when>
                                                    <c:when test="${order.paymentMethod eq 'vnpay'}">
                                                        <div class="method-icon method-vnpay">
                                                            <i class="fas fa-credit-card"></i>
                                                        </div>
                                                        VNPay
                                                    </c:when>
                                                    <c:when test="${order.paymentMethod eq 'banking'}">
                                                        <div class="method-icon method-banking">
                                                            <i class="fas fa-university"></i>
                                                        </div>
                                                        Banking
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="method-icon" style="background: #6c757d;">
                                                            <i class="fas fa-question"></i>
                                                        </div>
                                                        ${order.paymentMethod}
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                        <td>
                                            <strong class="text-danger">
                                                <fmt:formatNumber value="${order.amount}" pattern="#,###"/> VNĐ
                                            </strong>
                                        </td>
                                        <td>
                                            <strong class="text-success">
                                                <fmt:formatNumber value="${order.coinsAmount}" pattern="#,###"/> xu
                                            </strong>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.status eq 'SUCCESS'}">
                                                    <span class="status-badge status-success">
                                                        <i class="fas fa-check"></i> Thành công
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.status eq 'PENDING'}">
                                                    <span class="status-badge status-pending">
                                                        <i class="fas fa-clock"></i> Chờ thanh toán
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.status eq 'FAILED'}">
                                                    <span class="status-badge status-failed">
                                                        <i class="fas fa-times"></i> Thất bại
                                                    </span>
                                                </c:when>
                                                <c:when test="${order.status eq 'EXPIRED'}">
                                                    <span class="status-badge status-expired">
                                                        <i class="fas fa-hourglass-end"></i> Hết hạn
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${order.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <button type="button" class="btn btn-sm btn-outline-primary" 
                                                        onclick="viewOrderDetail('${order.orderCode}')" 
                                                        title="Xem chi tiết">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <c:if test="${order.status eq 'PENDING'}">
                                                    <c:choose>
                                                        <c:when test="${order.paymentMethod eq 'banking'}">
                                                            <a href="payment-banking?orderCode=${order.orderCode}" 
                                                               class="btn btn-sm btn-success" title="Thanh toán">
                                                                <i class="fas fa-university"></i>
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button type="button" class="btn btn-sm btn-success" 
                                                                    onclick="retryPayment('${order.orderCode}')" 
                                                                    title="Thanh toán lại">
                                                                <i class="fas fa-redo"></i>
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                                <c:if test="${order.status eq 'FAILED'}">
                                                    <button type="button" class="btn btn-sm btn-warning" 
                                                            onclick="retryPayment('${order.orderCode}')" 
                                                            title="Thử lại">
                                                        <i class="fas fa-redo"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Order Detail Modal -->
    <div class="modal fade" id="orderDetailModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content order-detail-modal">
                <div class="modal-header order-detail-header">
                    <h5 class="modal-title"><i class="fas fa-receipt"></i> Chi Tiết Đơn Hàng</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body" id="orderDetailContent">
                    <!-- Content will be loaded here -->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.11.5/js/dataTables.bootstrap5.min.js"></script>
    <script>
        $(document).ready(function() {
            // Initialize DataTable
            const table = $('#paymentTable').DataTable({
                language: {
                    url: 'https://cdn.datatables.net/plug-ins/1.11.5/i18n/vi.json'
                },
                order: [[5, 'desc']], // Sort by created date desc
                pageLength: 10,
                responsive: true,
                columnDefs: [
                    { orderable: false, targets: [6] } // Disable sorting for action column
                ]
            });

            // Search functionality
            $('#searchInput').on('keyup', function() {
                table.search(this.value).draw();
            });
        });

        // Filter by status
        function filterByStatus(status) {
            const table = $('#paymentTable').DataTable();
            
            // Update active button
            $('.btn-filter').removeClass('active');
            $(`[data-status="${status}"]`).addClass('active');
            
            if (status === 'all') {
                table.column(4).search('').draw();
            } else {
                table.column(4).search(status).draw();
            }
        }

        // View order detail
        function viewOrderDetail(orderCode) {
            $.ajax({
                url: 'payment-history',
                method: 'GET',
                data: { 
                    action: 'detail',
                    orderCode: orderCode 
                },
                success: function(response) {
                    $('#orderDetailContent').html(response);
                    $('#orderDetailModal').modal('show');
                },
                error: function() {
                    alert('Có lỗi xảy ra khi tải thông tin đơn hàng!');
                }
            });
        }

        // Retry payment
        function retryPayment(orderCode) {
            if (confirm('Bạn có muốn thử thanh toán lại đơn hàng này?')) {
                $.ajax({
                    url: 'payment',
                    method: 'POST',
                    data: { 
                        action: 'retry',
                        orderCode: orderCode 
                    },
                    success: function(response) {
                        if (response.success) {
                            if (response.paymentUrl) {
                                window.open(response.paymentUrl, '_blank');
                            } else {
                                window.location.href = response.redirectUrl;
                            }
                        } else {
                            alert(response.message || 'Có lỗi xảy ra!');
                        }
                    },
                    error: function() {
                        alert('Có lỗi xảy ra khi thử lại thanh toán!');
                    }
                });
            }
        }

        // Auto refresh pending orders every 30 seconds
        setInterval(function() {
            const hasPendingOrders = $('[data-status="PENDING"]').length > 0;
            if (hasPendingOrders) {
                // Check for status updates without full page reload
                $.ajax({
                    url: 'payment-history',
                    method: 'GET',
                    data: { action: 'checkPending' },
                    success: function(response) {
                        if (response.hasUpdates) {
                            location.reload();
                        }
                    }
                });
            }
        }, 30000);
    </script>
</body>
</html>
