<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý tin đăng - PhongTro247</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid mt-4">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3">
                <div class="card">
                    <div class="card-header">
                        <h5><i class="fas fa-list"></i> Menu quản lý</h5>
                    </div>
                    <div class="list-group list-group-flush">
                        <a href="/listing-management" class="list-group-item list-group-item-action active">
                            <i class="fas fa-home"></i> Tin đăng của tôi
                        </a>
                        <a href="/facebook-config" class="list-group-item list-group-item-action">
                            <i class="fab fa-facebook"></i> Cấu hình Facebook
                        </a>
                        <a href="/statistics" class="list-group-item list-group-item-action">
                            <i class="fas fa-chart-bar"></i> Thống kê
                        </a>
                    </div>
                </div>
            </div>
            
            <!-- Main content -->
            <div class="col-md-9">
                <!-- Listings Table -->
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5><i class="fas fa-home"></i> Tin đăng của bạn</h5>
                        <button class="btn btn-primary btn-sm" onclick="window.location.href='/create-listing'">
                            <i class="fas fa-plus"></i> Đăng tin mới
                        </button>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tiêu đề</th>
                                        <th>Địa chỉ</th>
                                        <th>Giá thuê</th>
                                        <th>Trạng thái</th>
                                        <th>Auto FB</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="listing" items="${userListings}">
                                        <tr>
                                            <td>${listing.listingId}</td>
                                            <td>
                                                <a href="/listing/${listing.listingId}" target="_blank">
                                                    ${listing.title}
                                                </a>
                                            </td>
                                            <td>
                                                ${listing.addressDistrict}, ${listing.addressCity}
                                            </td>
                                            <td>
                                                <fmt:formatNumber value="${listing.pricePerMonth}" 
                                                    type="currency" currencySymbol="₫" />
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${listing.status == 'pending'}">
                                                        <span class="badge bg-warning">Chờ duyệt</span>
                                                    </c:when>
                                                    <c:when test="${listing.status == 'approved'}">
                                                        <span class="badge bg-success">Đã duyệt</span>
                                                    </c:when>
                                                    <c:when test="${listing.status == 'rejected'}">
                                                        <span class="badge bg-danger">Bị từ chối</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">${listing.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="form-check form-switch">
                                                    <input class="form-check-input" type="checkbox" 
                                                           id="autoPost${listing.listingId}"
                                                           ${listing.autoPostSocial ? 'checked' : ''}
                                                           onchange="toggleAutoPost(${listing.listingId}, this.checked)">
                                                </div>
                                            </td>
                                            <td>
                                                <div class="btn-group btn-group-sm" role="group">
                                                    <c:if test="${listing.status == 'pending'}">
                                                        <button class="btn btn-outline-success" 
                                                                onclick="approveListing(${listing.listingId})">
                                                            <i class="fas fa-check"></i> Duyệt
                                                        </button>
                                                    </c:if>
                                                    <c:if test="${listing.status == 'approved'}">
                                                        <button class="btn btn-outline-primary" 
                                                                onclick="postToFacebook(${listing.listingId})">
                                                            <i class="fab fa-facebook"></i> Đăng FB
                                                        </button>
                                                    </c:if>
                                                    <button class="btn btn-outline-info" 
                                                            onclick="editListing(${listing.listingId})">
                                                        <i class="fas fa-edit"></i> Sửa
                                                    </button>
                                                    <button class="btn btn-outline-danger" 
                                                            onclick="deleteListing(${listing.listingId})">
                                                        <i class="fas fa-trash"></i> Xóa
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    
                                    <c:if test="${empty userListings}">
                                        <tr>
                                            <td colspan="7" class="text-center text-muted">
                                                <i class="fas fa-inbox fa-3x mb-3"></i>
                                                <br>Bạn chưa có tin đăng nào
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
                <!-- Activity Logs -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h5><i class="fas fa-history"></i> Hoạt động gần đây</h5>
                    </div>
                    <div class="card-body">
                        <div class="timeline">
                            <c:forEach var="log" items="${recentLogs}">
                                <div class="timeline-item mb-3">
                                    <div class="d-flex">
                                        <div class="flex-shrink-0">
                                            <c:choose>
                                                <c:when test="${log.actionType == 'FACEBOOK_POST'}">
                                                    <i class="fab fa-facebook-square text-primary"></i>
                                                </c:when>
                                                <c:when test="${log.actionType == 'LISTING_CREATED'}">
                                                    <i class="fas fa-plus-circle text-success"></i>
                                                </c:when>
                                                <c:when test="${log.actionType == 'LISTING_APPROVED'}">
                                                    <i class="fas fa-check-circle text-success"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-info-circle text-info"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="flex-grow-1 ms-3">
                                            <h6 class="mb-1">${log.summary}</h6>
                                            <p class="text-muted mb-1">
                                                <fmt:formatDate value="${log.createdAt}" 
                                                    pattern="dd/MM/yyyy HH:mm:ss" />
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                            
                            <c:if test="${empty recentLogs}">
                                <p class="text-muted text-center">Chưa có hoạt động nào</p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Toggle auto post Facebook
        function toggleAutoPost(listingId, enabled) {
            fetch('/toggle-auto-post', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `listingId=${listingId}&enabled=${enabled}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showAlert('success', data.message);
                } else {
                    showAlert('danger', data.message);
                    // Revert checkbox if failed
                    document.getElementById(`autoPost${listingId}`).checked = !enabled;
                }
            })
            .catch(error => {
                showAlert('danger', 'Có lỗi xảy ra: ' + error.message);
                document.getElementById(`autoPost${listingId}`).checked = !enabled;
            });
        }
        
        // Approve listing
        function approveListing(listingId) {
            if (confirm('Bạn có chắc muốn duyệt tin đăng này?')) {
                fetch('/approve-listing', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `listingId=${listingId}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showAlert('success', data.message);
                        setTimeout(() => location.reload(), 1500);
                    } else {
                        showAlert('danger', data.message);
                    }
                })
                .catch(error => {
                    showAlert('danger', 'Có lỗi xảy ra: ' + error.message);
                });
            }
        }
        
        // Post to Facebook manually
        function postToFacebook(listingId) {
            if (confirm('Đăng tin này lên Facebook Page?')) {
                fetch('/facebook-post', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `listingId=${listingId}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showAlert('success', 'Đăng Facebook thành công! Post ID: ' + data.postId);
                    } else {
                        showAlert('danger', 'Đăng Facebook thất bại: ' + data.message);
                    }
                })
                .catch(error => {
                    showAlert('danger', 'Có lỗi xảy ra: ' + error.message);
                });
            }
        }
        
        // Show alert
        function showAlert(type, message) {
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
            alertDiv.style.top = '20px';
            alertDiv.style.right = '20px';
            alertDiv.style.zIndex = '9999';
            alertDiv.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            
            document.body.appendChild(alertDiv);
            
            // Auto hide after 5 seconds
            setTimeout(() => {
                if (alertDiv.parentNode) {
                    alertDiv.parentNode.removeChild(alertDiv);
                }
            }, 5000);
        }
        
        // Other functions
        function editListing(listingId) {
            window.location.href = `/edit-listing/${listingId}`;
        }
        
        function deleteListing(listingId) {
            if (confirm('Bạn có chắc muốn xóa tin đăng này?')) {
                // Implement delete functionality
                alert('Chức năng xóa tin đăng sẽ được implement sau');
            }
        }
    </script>
</body>
</html>
