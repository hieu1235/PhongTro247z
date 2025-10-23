<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng tin mới - PhongTro247</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

        <style>
            :root{
                --sidebar-width:230px;
                --primary:#007bff;
                --success:#28a745;
                --warning:#ffc107;
                --danger:#dc3545;
                --light:#f8f9fa;
                --dark:#343a40;
                --radius:8px;
                --transition:all 0.3s ease
            }
            *{
                margin:0;
                padding:0;
                box-sizing:border-box
            }
            body{
                font-family:'Segoe UI',sans-serif;
                background:#f5f5f5
            }
            .wrapper{
                display:flex;
                min-height:100vh
            }

            /* Sidebar */
            .sidebar{
                width:var(--sidebar-width);
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
                position:fixed;
                height:100vh;
                left:0;
                top:0;
                transition:var(--transition);
                z-index:1000;
                overflow-y:auto
            }
            .sidebar.collapsed{
                margin-left:calc(-1 * var(--sidebar-width))
            }
            .sidebar-header{
                padding:20px;
                border-bottom:1px solid rgba(255,255,255,0.2)
            }
            .user-info{
                display:flex;
                align-items:center;
                gap:12px
            }
            .user-avatar{
                width:50px;
                height:50px;
                border-radius:50%;
                background:rgba(255,255,255,0.2);
                display:flex;
                align-items:center;
                justify-content:center;
                font-size:24px
            }
            .user-details h5{
                margin:0;
                font-size:16px;
                font-weight:600
            }
            .user-details small{
                opacity:0.8
            }

            .sidebar-menu{
                padding:20px 0
            }
            .sidebar-menu ul{
                list-style:none
            }
            .sidebar-menu a{
                color:rgba(255,255,255,0.9);
                padding:12px 20px;
                display:flex;
                align-items:center;
                gap:12px;
                text-decoration:none;
                transition:var(--transition)
            }
            .sidebar-menu a:hover,.sidebar-menu a.active{
                background:rgba(255,255,255,0.1);
                color:white;
                transform:translateX(5px)
            }
            .sidebar-menu i{
                width:20px;
                text-align:center
            }

            /* Main Content */
            .main-content{
                flex:1;
                margin-left:var(--sidebar-width);
                transition:var(--transition)
            }
            .main-content.expanded{
                margin-left:0
            }

            /* Header */
            .top-nav{
                background:white;
                padding:15px 20px;
                box-shadow:0 2px 4px rgba(0,0,0,0.1);
                display:flex;
                align-items:center;
                justify-content:space-between
            }
            .sidebar-toggle{
                background:white;
                border:1px solid #dee2e6;
                border-radius:4px;
                padding:8px 12px;
                cursor:pointer
            }
            .nav-right{
                display:flex;
                align-items:center;
                gap:15px
            }
            .notification{
                position:relative;
                cursor:pointer
            }
            .notification .badge{
                position:absolute;
                top:-8px;
                right:-8px;
                background:var(--danger);
                color:white;
                border-radius:50%;
                width:18px;
                height:18px;
                font-size:10px;
                display:flex;
                align-items:center;
                justify-content:center
            }

            /* Content */
            .content{
                padding:20px
            }

            /* Alerts */
            .alert{
                padding:12px 16px;
                border-radius:var(--radius);
                margin-bottom:20px;
                display:flex;
                align-items:center;
                gap:10px;
                position:relative
            }
            .alert-success{
                background:#d4edda;
                color:#155724;
                border:1px solid #c3e6cb
            }
            .alert-error{
                background:#f8d7da;
                color:#721c24;
                border:1px solid #f5c6cb
            }
            .alert-close{
                position:absolute;
                right:10px;
                background:none;
                border:none;
                font-size:18px;
                cursor:pointer;
                opacity:0.5
            }
            .alert-close:hover{
                opacity:1
            }

            /* Form Styles */
            .form-section{
                background:white;
                border-radius:var(--radius);
                box-shadow:0 2px 10px rgba(0,0,0,0.1);
                animation:fadeInUp 0.6s ease forwards
            }
            .form-header{
                padding:20px;
                border-bottom:1px solid #e9ecef;
                display:flex;
                justify-content:between;
                align-items:center
            }
            .form-header h2{
                margin:0;
                display:flex;
                align-items:center;
                gap:10px;
                color:var(--dark)
            }
            .form-body{
                padding:20px
            }

            /* ✅ PRO STATUS SECTION STYLES */
            .pro-status-section {
                margin-bottom: 20px;
            }
            
            .pro-status-card {
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                border-radius: var(--radius);
                padding: 20px;
                border: 2px solid #dee2e6;
                transition: var(--transition);
            }
            
            .pro-status-card.pro-plan {
                background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
                border-color: #ffc107;
                box-shadow: 0 4px 15px rgba(255, 193, 7, 0.2);
            }
            
            .pro-status-header {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 15px;
            }
            
            .pro-status-header i {
                font-size: 1.2em;
                color: #6c757d;
            }
            
            .pro-plan .pro-status-header i {
                color: #f39c12;
            }
            
            .plan-name {
                font-size: 1.1em;
                font-weight: 600;
                color: var(--dark);
            }
            
            .plan-badge {
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 0.75em;
                font-weight: 700;
                text-transform: uppercase;
                margin-left: auto;
            }
            
            .plan-badge.free {
                background: #6c757d;
                color: white;
            }
            
            .plan-badge.pro {
                background: linear-gradient(45deg, #f39c12, #e67e22);
                color: white;
                box-shadow: 0 2px 8px rgba(243, 156, 18, 0.3);
            }
            
            .pro-status-details {
                display: flex;
                flex-wrap: wrap;
                gap: 15px;
                margin-bottom: 15px;
            }
            
            .status-item {
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 0.9em;
                color: #495057;
            }
            
            .status-item i {
                width: 16px;
                text-align: center;
                color: #6c757d;
            }
            
            .text-success {
                color: var(--success) !important;
            }
            
            .text-danger {
                color: var(--danger) !important;
            }
            
            .upgrade-section {
                background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
                border-radius: 8px;
                padding: 15px;
                text-align: center;
                margin-top: 15px;
            }
            
            .upgrade-text {
                margin: 0 0 10px 0;
                color: #1976d2;
                font-weight: 500;
            }
            
            .btn-upgrade {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                background: linear-gradient(45deg, #f39c12, #e67e22);
                color: white;
                text-decoration: none;
                padding: 10px 20px;
                border-radius: 25px;
                font-weight: 600;
                transition: var(--transition);
                box-shadow: 0 4px 15px rgba(243, 156, 18, 0.3);
            }
            
            .btn-upgrade:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
                text-decoration: none;
                color: white;
            }
            
            .warning-section {
                background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
                border-left: 4px solid #ff9800;
                padding: 12px 15px;
                border-radius: 0 8px 8px 0;
                margin-top: 15px;
                display: flex;
                align-items: center;
                gap: 10px;
                color: #e65100;
                font-weight: 500;
                font-size: 0.9em;
            }
            
            .warning-section i {
                color: #ff9800;
                font-size: 1.1em;
            }

            .form-grid{
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:30px
            }
            .form-column h3{
                color:var(--primary);
                margin-bottom:20px;
                display:flex;
                align-items:center;
                gap:8px;
                font-size:18px
            }

            .form-group{
                margin-bottom:20px
            }
            .form-group label{
                display:block;
                margin-bottom:8px;
                font-weight:600;
                color:var(--dark);
                font-size:14px
            }
            .form-group label i{
                margin-right:8px;
                color:var(--primary)
            }
            .required{
                color:var(--danger)
            }

            .form-control{
                width:100%;
                padding:12px;
                border:1px solid #ddd;
                border-radius:6px;
                font-size:14px;
                transition:var(--transition)
            }
            .form-control:focus{
                outline:none;
                border-color:var(--primary);
                box-shadow:0 0 0 3px rgba(0,123,255,0.1)
            }
            .form-control[type="number"]{
                text-align:right
            }

            textarea.form-control{
                resize:vertical;
                min-height:120px
            }

            .form-row{
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:15px
            }

            .form-help{
                font-size:12px;
                color:#6c757d;
                margin-top:4px;
                display:flex;
                align-items:center;
                gap:4px
            }

            /* File Upload */
            .file-input-container{
                position:relative;
                border:2px dashed #ddd;
                border-radius:8px;
                padding:30px;
                text-align:center;
                transition:var(--transition);
                cursor:pointer
            }
            .file-input-container:hover{
                border-color:var(--primary);
                background:rgba(0,123,255,0.05)
            }
            .file-input-container.dragover{
                border-color:var(--primary);
                background:rgba(0,123,255,0.1)
            }

            .file-input{
                position:absolute;
                top:0;
                left:0;
                width:100%;
                height:100%;
                opacity:0;
                cursor:pointer
            }

            .file-input-label{
                color:#6c757d;
                font-size:14px
            }
            .file-input-label i{
                font-size:32px;
                margin-bottom:10px;
                color:var(--primary)
            }

            .images-info{
                margin-top:10px;
                padding:10px;
                background:#f8f9fa;
                border-radius:6px;
                display:none
            }
            .images-info.show{
                display:block
            }
            .files-selected{
                color:var(--success);
                font-weight:600
            }

            .image-preview{
                margin-top:15px;
                display:grid;
                grid-template-columns:repeat(auto-fill,minmax(150px,1fr));
                gap:10px
            }
            .preview-item{
                position:relative;
                border-radius:8px;
                overflow:hidden;
                box-shadow:0 2px 8px rgba(0,0,0,0.1)
            }
            .preview-item img{
                width:100%;
                height:100px;
                object-fit:cover
            }
            .preview-info{
                padding:8px;
                background:white;
                font-size:11px;
                color:#666
            }
            .file-name{
                display:block;
                font-weight:600;
                margin-bottom:2px
            }
            .file-size{
                color:#999
            }
            .primary-badge{
                position:absolute;
                top:5px;
                left:5px;
                background:var(--success);
                color:white;
                font-size:10px;
                padding:2px 6px;
                border-radius:4px
            }

            /* ✅ Facebook Pages Selection Styles */
            .facebook-pages-selection{
                border:1px solid #ddd;
                border-radius:8px;
                max-height:300px;
                overflow-y:auto;
                background:#f9f9f9
            }

            .page-checkbox{
                padding:12px;
                border-bottom:1px solid #eee;
                transition:var(--transition)
            }

            .page-checkbox:last-child{
                border-bottom:none
            }

            .page-checkbox:hover{
                background:#f0f0f0
            }

            .checkbox-label{
                display:flex;
                align-items:center;
                gap:12px;
                cursor:pointer;
                margin:0;
                font-weight:normal
            }

            .checkbox-label input[type="checkbox"]{
                width:18px;
                height:18px;
                cursor:pointer
            }

            .page-info{
                flex:1
            }

            .page-info strong{
                color:var(--primary);
                display:block;
                font-size:14px
            }

            .page-info small{
                color:#666;
                display:block;
                margin-top:2px;
                font-size:12px
            }

            .page-badges{
                margin-top:4px;
                display:flex;
                gap:4px;
                flex-wrap:wrap
            }

            .badge{
                font-size:10px;
                padding:2px 6px;
                border-radius:4px;
                color:white;
                font-weight:600
            }

            .badge-primary{background:var(--primary)}
            .badge-secondary{background:#6c757d}
            .badge-warning{background:var(--warning);color:#000}
            .badge-success{background:var(--success)}

            .no-facebook-pages{
                text-align:center;
                padding:30px;
                color:#6c757d;
                border:2px dashed #ddd;
                border-radius:8px;
                background:#f8f9fa
            }

            .no-facebook-pages i{
                font-size:48px;
                margin-bottom:15px;
                color:#ddd
            }

            .selected-pages-info{
                color:var(--success);
                font-weight:600;
                padding:8px;
                background:rgba(40,167,69,0.1);
                border-radius:4px;
                margin-top:10px;
                display:none
            }

            .selected-pages-info.show{
                display:block
            }

            /* Buttons */
            .btn{
                padding:12px 20px;
                border:none;
                border-radius:6px;
                font-weight:600;
                text-decoration:none;
                display:inline-flex;
                align-items:center;
                gap:8px;
                cursor:pointer;
                transition:var(--transition);
                font-size:14px
            }
            .btn-primary{
                background:var(--primary);
                color:white
            }
            .btn-primary:hover{
                background:#0056b3;
                color:white;
                transform:translateY(-1px)
            }
            .btn-secondary{
                background:#6c757d;
                color:white
            }
            .btn-secondary:hover{
                background:#545b62
            }
            .btn-sm{
                padding:4px 8px;
                font-size:10px
                   
            }
            .btn:disabled{
                opacity:0.6;
                cursor:not-allowed
            }

            .form-actions{
                display:flex;
                gap:10px;
                justify-content:flex-end;
                margin-top:30px;
                padding-top:20px;
                border-top:1px solid #e9ecef
            }

            /* Loading state */
            .btn-loading{
                position:relative;
                color:transparent!important
            }
            .btn-loading::after{
                content:'';
                position:absolute;
                top:50%;
                left:50%;
                margin:-10px 0 0 -10px;
                width:20px;
                height:20px;
                border:2px solid transparent;
                border-top:2px solid currentColor;
                border-radius:50%;
                animation:spin 1s linear infinite
            }

            @keyframes spin{
                to{
                    transform:rotate(360deg)
                }
            }

            /* Responsive */
            @media (max-width:768px){
                .sidebar{
                    margin-left:calc(-1 * var(--sidebar-width))
                }
                .sidebar.show{
                    margin-left:0
                }
                .main-content{
                    margin-left:0
                }
                .form-grid{
                    grid-template-columns:1fr
                }
                .form-row{
                    grid-template-columns:1fr
                }
                .content{
                    padding:10px
                }
                .form-body{
                    padding:15px
                }
                .form-actions{
                    flex-direction:column
                }
                .checkbox-label{
                    flex-direction:column;
                    align-items:flex-start;
                    gap:8px
                }
                .page-badges{
                    justify-content:flex-start
                }
            }

            /* Animations */
            @keyframes fadeInUp{
                from{
                    opacity:0;
                    transform:translateY(30px)
                }
                to{
                    opacity:1;
                    transform:translateY(0)
                }
            }

            /* Scrollbar */
            .sidebar::-webkit-scrollbar{
                width:6px
            }
            .sidebar::-webkit-scrollbar-track{
                background:rgba(255,255,255,0.1)
            }
            .sidebar::-webkit-scrollbar-thumb{
                background:rgba(255,255,255,0.3);
                border-radius:3px
            }
            .sidebar::-webkit-scrollbar-thumb:hover{
                background:rgba(255,255,255,0.5)
            }

            .facebook-pages-selection::-webkit-scrollbar{
                width:6px
            }
            .facebook-pages-selection::-webkit-scrollbar-track{
                background:#f1f1f1
            }
            .facebook-pages-selection::-webkit-scrollbar-thumb{
                background:#c1c1c1;
                border-radius:3px
            }

            /* Facebook Pages Section */
            .facebook-section {
                background: white;
                border-radius: var(--radius);
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 24px;
                overflow: hidden;
                border: 1px solid #e9ecef;
            }

            .facebook-header {
                background: linear-gradient(135deg, #1877f2, #42a5f5);
                color: white;
                padding: 20px;
                border-bottom: none;
            }

            .facebook-header h5 {
                margin: 0;
                font-size: 1.2rem;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .facebook-body {
                padding: 24px;
            }

            .facebook-description {
                color: #6c757d;
                margin-bottom: 20px;
                font-size: 0.95rem;
                line-height: 1.5;
            }

            .facebook-pages-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 16px;
                margin-bottom: 20px;
            }

            .facebook-page-item {
                border: 2px solid #e9ecef;
                border-radius: var(--radius);
                padding: 16px;
                transition: var(--transition);
                cursor: pointer;
            }

            .facebook-page-item:hover {
                border-color: #1877f2;
                box-shadow: 0 4px 12px rgba(24, 119, 242, 0.15);
            }

            .facebook-page-item.selected {
                border-color: #1877f2;
                background: #f8f9ff;
            }

            .facebook-check {
                display: flex;
                align-items: flex-start;
                gap: 12px;
            }

            .facebook-check input[type="checkbox"] {
                width: 20px;
                height: 20px;
                margin: 0;
                cursor: pointer;
                accent-color: #1877f2;
            }

            .facebook-page-info {
                flex: 1;
            }

            .facebook-page-name {
                font-weight: 600;
                color: #2c3e50;
                margin-bottom: 4px;
                font-size: 1rem;
            }

            .facebook-page-id {
                color: #6c757d;
                font-size: 0.85rem;
                margin-bottom: 8px;
            }

            .facebook-badges {
                display: flex;
                gap: 6px;
                flex-wrap: wrap;
            }

            .facebook-badge {
                display: inline-block;
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 0.75rem;
                font-weight: 500;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .badge-primary {
                background: #1877f2;
                color: white;
            }

            .badge-warning {
                background: #ffc107;
                color: #212529;
            }

            .facebook-alert {
                background: #e3f2fd;
                border: 1px solid #bbdefb;
                border-radius: var(--radius);
                padding: 16px;
                display: flex;
                align-items: flex-start;
                gap: 12px;
            }

            .facebook-alert i {
                color: #1976d2;
                font-size: 1.1rem;
                margin-top: 2px;
            }

            .facebook-alert-content {
                flex: 1;
            }

            .facebook-alert strong {
                color: #1565c0;
                font-weight: 600;
            }

            .facebook-empty {
                text-align: center;
                padding: 40px 24px;
                background: white;
                border-radius: var(--radius);
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 24px;
            }

            .facebook-empty i {
                color: #adb5bd;
                margin-bottom: 16px;
            }

            .facebook-empty h6 {
                color: #495057;
                margin-bottom: 12px;
                font-weight: 600;
            }

            .facebook-empty p {
                color: #6c757d;
                margin-bottom: 20px;
                line-height: 1.5;
            }

            @media (max-width: 768px) {
                .facebook-pages-grid {
                    grid-template-columns: 1fr;
                }
                
                .facebook-body {
                    padding: 16px;
                }
                
                .facebook-header {
                    padding: 16px;
                }
            }

            /* ✅ SCHEDULED POSTING STYLES */
            .scheduled-section {
                background: white;
                border-radius: var(--radius);
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            .scheduled-header {
                display: flex;
                align-items: center;
                margin-bottom: 15px;
                padding-bottom: 10px;
                border-bottom: 1px solid #e9ecef;
            }

            .scheduled-header h5 {
                margin: 0;
                color: var(--dark);
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .scheduled-header i {
                color: var(--primary);
                font-size: 18px;
            }

            .scheduled-body {
                margin-top: 15px;
            }

            .scheduled-description p {
                color: #6c757d;
                margin-bottom: 15px;
                font-size: 14px;
            }

            .scheduled-options {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 15px;
                margin-bottom: 20px;
            }

            .scheduled-option {
                display: flex;
                align-items: flex-start;
                padding: 15px;
                border: 2px solid #e9ecef;
                border-radius: var(--radius);
                cursor: pointer;
                transition: var(--transition);
                background: #f8f9fa;
            }

            .scheduled-option:hover {
                border-color: var(--primary);
                background: white;
            }

            .scheduled-option input[type="radio"] {
                margin-right: 12px;
                margin-top: 2px;
            }

            .scheduled-option input[type="radio"]:checked ~ .option-content {
                color: var(--primary);
            }

            .scheduled-option:has(input:checked) {
                border-color: var(--primary);
                background: white;
                box-shadow: 0 0 0 3px rgba(0,123,255,0.1);
            }

            .option-content {
                display: flex;
                align-items: flex-start;
                gap: 10px;
                flex: 1;
            }

            .option-content i {
                font-size: 20px;
                margin-top: 2px;
            }

            .option-text strong {
                display: block;
                margin-bottom: 4px;
                font-weight: 600;
            }

            .option-text small {
                color: #6c757d;
                font-size: 13px;
                line-height: 1.3;
            }

            .scheduled-datetime {
                background: #f8f9fa;
                border: 1px solid #dee2e6;
                border-radius: var(--radius);
                padding: 20px;
                margin-bottom: 15px;
            }

            .scheduled-info {
                background: #e3f2fd;
                border: 1px solid #bbdefb;
                border-radius: var(--radius);
                padding: 15px;
            }

            .info-card {
                display: flex;
                gap: 12px;
            }

            .info-card i {
                color: #1976d2;
                font-size: 18px;
                margin-top: 2px;
            }

            .info-content strong {
                display: block;
                color: #1976d2;
                margin-bottom: 8px;
                font-weight: 600;
            }

            .info-content ul {
                margin: 0;
                padding-left: 20px;
                color: #37474f;
            }

            .info-content li {
                margin-bottom: 4px;
                font-size: 14px;
            }
        </style>
    </head>
    <body>
        <div class="wrapper">
            <!-- Sidebar -->
            <nav class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <div class="user-info">
                        <div class="user-avatar"><i class="fas fa-user-circle"></i></div>
                        <div class="user-details">
                            <h5>${user.fullName}</h5>
                            <small>${user.roleName}</small>
                        </div>
                    </div>
                </div>

                <div class="sidebar-menu">
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-tachometer-alt"></i><span>Dashboard</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-list-alt"></i><span>Danh sách tin đăng</span></a></li>
                        <li><a class="active" href="${pageContext.request.contextPath}/post/create"><i class="fas fa-plus-circle"></i><span>Đăng tin mới</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/subscription"><i class="fas fa-crown"></i><span>Gói Pro</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/facebook/manage"><i class="fab fa-facebook"></i><span>Cấu hình Facebook</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i><span>Xem trang chủ</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user-cog"></i><span>Cài đặt tài khoản</span></a></li>
                        <li class="logout"><a href="/PhongTroNew/logout" onclick="return confirm('Bạn có chắc chắn muốn đăng xuất?')"><i class="fas fa-sign-out-alt"></i><span>Đăng xuất</span></a></li>
                    </ul>
                </div>
            </nav>

            <!-- Main Content -->
            <div class="main-content">
                <!-- Top Navigation -->
                <header class="top-nav">
                    <button class="sidebar-toggle" id="sidebarToggle"><i class="fas fa-bars"></i></button>
                    <h1>Đăng tin mới</h1>
                    <div class="nav-right">
                        <div class="notification"><i class="fas fa-bell"></i><span class="badge">3</span></div>
                    </div>
                </header>

                <!-- Content Area -->
                <main class="content">
                    <!-- Alert Messages -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-error">
                            <i class="fas fa-exclamation-circle"></i>
                            <span>${error}</span>
                            <button class="alert-close" onclick="this.parentElement.style.display = 'none'">&times;</button>
                        </div>
                    </c:if>

                    <!-- Form Section -->
                    <div class="form-section">
                        <div class="form-header">
                            <h2><i class="fas fa-plus-circle"></i>Đăng tin phòng trọ mới</h2>
                        </div>

                        <!-- ✅ PRO STATUS INFO SECTION -->
                        <div class="pro-status-section">
                            <c:if test="${not empty subscription}">
                                <div class="pro-status-card ${subscription.planName == 'Free' ? 'free-plan' : 'pro-plan'}">
                                    <div class="pro-status-header">
                                        <c:choose>
                                            <c:when test="${subscription.planName == 'Free'}">
                                                <i class="fas fa-user"></i>
                                                <span class="plan-name">Gói Miễn Phí</span>
                                                <span class="plan-badge free">FREE</span>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-crown"></i>
                                                <span class="plan-name">${subscription.planName}</span>
                                                <span class="plan-badge pro">PRO</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    
                                    <div class="pro-status-details">
                                        <div class="status-item">
                                            <i class="fas fa-calendar-day"></i>
                                            <span>Hôm nay: <strong>${remainingPosts}/${subscription.dailyPostLimit}</strong> bài</span>
                                        </div>
                                        
                                        <c:if test="${not empty balance}">
                                            <div class="status-item">
                                                <i class="fas fa-coins"></i>
                                                <span>Số xu: <strong>${balance.coinsAsDouble}</strong> xu</span>
                                            </div>
                                        </c:if>
                                        
                                        <div class="status-item">
                                            <i class="fas fa-facebook"></i>
                                            <span>Facebook: 
                                                <c:choose>
                                                    <c:when test="${subscription.facebookPosting}">
                                                        <strong class="text-success">Được phép</strong>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <strong class="text-danger">Không được phép</strong>
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <!-- Upgrade button for Free users -->
                                    <c:if test="${subscription.planName == 'Free'}">
                                        <div class="upgrade-section">
                                            <p class="upgrade-text">
                                                <i class="fas fa-arrow-up"></i>
                                                Nâng cấp Pro để đăng <strong>10 bài/ngày</strong> và <strong>đăng Facebook</strong>!
                                            </p>
                                            <a href="${pageContext.request.contextPath}/subscription" class="btn-upgrade">
                                                <i class="fas fa-crown"></i> Nâng cấp Pro - 100 xu
                                            </a>
                                        </div>
                                    </c:if>
                                    
                                    <!-- Warning if nearly out of posts -->
                                    <c:if test="${remainingPosts <= 2 && subscription.planName == 'Free'}">
                                        <div class="warning-section">
                                            <i class="fas fa-exclamation-triangle"></i>
                                            <span>Bạn sắp hết lượt đăng bài miễn phí. Nâng cấp Pro để tiếp tục!</span>
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>
                        </div>

                        <div class="form-body">
                            <form action="${pageContext.request.contextPath}/post/create" method="post" enctype="multipart/form-data" id="postForm">
                                <div class="form-grid">
                                    <!-- Left Column -->
                                    <div class="form-column">
                                        <h3><i class="fas fa-info-circle"></i>Thông tin cơ bản</h3>

                                        <div class="form-group">
                                            <label for="title">
                                                <i class="fas fa-heading"></i>
                                                Tiêu đề tin đăng <span class="required">*</span>
                                            </label>
                                            <input type="text" 
                                                   id="title" 
                                                   name="title" 
                                                   class="form-control"
                                                   placeholder="VD: Phòng trọ giá rẻ quận 1, đầy đủ tiện nghi" 
                                                   value="${param.title}"
                                                   required 
                                                   maxlength="200"/>
                                            <div class="form-help">
                                                <i class="fas fa-lightbulb"></i>
                                                Tiêu đề hấp dẫn sẽ thu hút nhiều người quan tâm hơn
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="content">
                                                <i class="fas fa-align-left"></i>
                                                Mô tả chi tiết
                                            </label>
                                            <textarea id="content" 
                                                      name="content" 
                                                      class="form-control"
                                                      placeholder="Mô tả chi tiết về phòng trọ: diện tích, tiện ích, quy định...">${param.content}</textarea>
                                            <div class="form-help">
                                                <i class="fas fa-info-circle"></i>
                                                Mô tả càng chi tiết càng tốt để người thuê hiểu rõ hơn
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="address">
                                                <i class="fas fa-map-marker-alt"></i>
                                                Địa chỉ
                                            </label>
                                            <input type="text" 
                                                   id="address" 
                                                   name="address" 
                                                   class="form-control"
                                                   placeholder="VD: 123 Nguyễn Văn Linh, Quận 7, TP.HCM"
                                                   value="${param.address}"/>
                                            <div class="form-help">
                                                <i class="fas fa-map"></i>
                                                Địa chỉ càng cụ thể càng dễ tìm
                                            </div>
                                        </div>

                                        <div class="form-row">
                                            <div class="form-group">
                                                <label for="lat">
                                                    <i class="fas fa-crosshairs"></i>
                                                    Latitude
                                                </label>
                                                <input type="text" 
                                                       id="lat" 
                                                       name="lat" 
                                                       class="form-control"
                                                       placeholder="10.776889"
                                                       value="${param.lat}"/>
                                            </div>
                                            <div class="form-group">
                                                <label for="lng">
                                                    <i class="fas fa-crosshairs"></i>
                                                    Longitude
                                                </label>
                                                <input type="text" 
                                                       id="lng" 
                                                       name="lng" 
                                                       class="form-control"
                                                       placeholder="106.695887"
                                                       value="${param.lng}"/>
                                            </div>
                                        </div>
                                        <div class="form-help">
                                            <i class="fas fa-info-circle"></i>
                                            Tọa độ giúp hiển thị chính xác vị trí trên bản đồ
                                        </div>
                                    </div>

                                    <!-- Right Column -->
                                    <div class="form-column">
                                        <h3><i class="fas fa-tags"></i>Giá cả & Diện tích</h3>

                                        <div class="form-row">
                                            <div class="form-group">
                                                <label for="price">
                                                    <i class="fas fa-money-bill-wave"></i>
                                                    Giá thuê (VNĐ/tháng)
                                                </label>
                                                <input type="number" 
                                                       id="price" 
                                                       name="price" 
                                                       class="form-control"
                                                       placeholder="3000000"
                                                       value="${param.price}"
                                                       step="10000" 
                                                       min="0"/>
                                                <div class="form-help">
                                                    <i class="fas fa-calculator"></i>
                                                    Giá thuê mỗi tháng tính bằng VNĐ
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <label for="area">
                                                    <i class="fas fa-ruler-combined"></i>
                                                    Diện tích (m²)
                                                </label>
                                                <input type="number" 
                                                       id="area" 
                                                       name="area" 
                                                       class="form-control"
                                                       placeholder="25"
                                                       value="${param.area}"
                                                       step="0.1" 
                                                       min="0"/>
                                                <div class="form-help">
                                                    <i class="fas fa-expand"></i>
                                                    Diện tích phòng tính bằng mét vuông
                                                </div>
                                            </div>
                                        </div>

                                        <!-- ✅ FACEBOOK PAGES SELECTION SECTION - FIXED EL EXPRESSIONS -->
                                        <h3><i class="fab fa-facebook"></i>Đăng lên Facebook Pages</h3>

                                        <!-- Debug information -->
                                        <!-- DEBUG: facebookPages size = ${not empty facebookPages ? facebookPages.size() : 'NULL or EMPTY'} -->

                                        <div class="form-group">
                                            <label>
                                                <i class="fab fa-facebook-square"></i>
                                                Chọn Pages để đăng tin (có thể chọn nhiều)
                                            </label>

                                            <c:choose>
                                                <c:when test="${not empty facebookPages}">
                                                    <div class="facebook-pages-selection">
                                                        <c:forEach var="page" items="${facebookPages}">
                                                            <div class="page-checkbox">
                                                                <label class="checkbox-label">
                                                                    <!-- ✅ FIXED: Sử dụng bracket notation thay vì dot notation -->
                                                                    <input type="checkbox" 
                                                                           name="selectedPages" 
                                                                           value="${page.pageId}"
                                                                           ${page['default'] ? 'checked' : ''}
                                                                           ${!page.autoPost ? 'disabled' : ''}
                                                                           onchange="updateSelectedPagesCount()">
                                                                    <div class="page-info">
                                                                        <strong>${page.pageName}</strong>
                                                                        <small>ID: ${page.pageId}</small>
                                                                        <div class="page-badges">
                                                                            <c:if test="${page['default']}">
                                                                                <span class="badge badge-primary">Mặc định</span>
                                                                            </c:if>
                                                                            <c:if test="${page.autoPost}">
                                                                                <span class="badge badge-success">Auto Post</span>
                                                                            </c:if>
                                                                            <c:if test="${!page.autoPost}">
                                                                                <span class="badge badge-secondary">Tắt auto</span>
                                                                            </c:if>
                                                                        </div>
                                                                    </div>
                                                                </label>
                                                            </div>
                                                        </c:forEach>
                                                    </div>
                                                    <div class="selected-pages-info" id="selectedPagesInfo">
                                                        <i class="fas fa-check-circle"></i>
                                                        Đã chọn <span id="selectedCount">0</span> page(s)
                                                    </div>
                                                    <div class="form-help">
                                                        <i class="fas fa-info-circle"></i>
                                                        Chỉ pages có "Auto Post" mới được đăng tự động khi AI duyệt. Pages bị tắt sẽ không thể chọn.
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="no-facebook-pages">
                                                        <i class="fab fa-facebook"></i>
                                                        <h4>Chưa cấu hình Facebook Pages</h4>
                                                        <p>Bạn cần thêm ít nhất một Facebook Page để có thể đăng tin tự động.</p>
                                                        <a href="${pageContext.request.contextPath}/facebook/manage" class="btn btn-primary btn-sm">
                                                            <i class="fas fa-cog"></i>
                                                            Cấu hình ngay
                                                        </a>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <h3><i class="fas fa-images"></i>Hình ảnh</h3>

                                        <div class="form-group">
                                            <label for="images">
                                                <i class="fas fa-camera"></i>
                                                Chọn ảnh phòng trọ (tối đa 5 ảnh)
                                            </label>
                                            <div class="file-input-container" ondrop="dropHandler(event);" ondragover="dragOverHandler(event);" ondragleave="dragLeaveHandler(event);">
                                                <input type="file" 
                                                       id="images" 
                                                       name="images" 
                                                       multiple 
                                                       accept="image/*" 
                                                       onchange="onImagesChange(this)"
                                                       class="file-input"/>
                                                <div class="file-input-label">
                                                    <i class="fas fa-cloud-upload-alt"></i>
                                                    <div>Kéo thả ảnh vào đây hoặc click để chọn</div>
                                                </div>
                                            </div>
                                            <div id="imagesInfo" class="images-info"></div>
                                            <div id="imagePreview" class="image-preview"></div>
                                            <div class="form-help">
                                                <i class="fas fa-info-circle"></i>
                                                Chọn ảnh chất lượng cao, rõ nét. Ảnh đầu tiên sẽ làm ảnh đại diện.
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Facebook Pages Selection Section -->
                                <c:if test="${not empty facebookPages}">
                                    <div class="facebook-section">
                                        <div class="facebook-header">
                                            <h5><i class="fab fa-facebook"></i> Chọn Facebook Pages để đăng</h5>
                                        </div>
                                        <div class="facebook-body">
                                            <p class="facebook-description">
                                                Chọn các Facebook Pages mà bạn muốn tự động đăng bài khi được duyệt:
                                            </p>
                                            
                                            <div class="facebook-pages-grid">
                                                <c:forEach var="fbPage" items="${facebookPages}" varStatus="status">
                                                    <div class="facebook-page-item" onclick="togglePage(this)">
                                                        <div class="facebook-check">
                                                            <input class="form-check-input" type="checkbox" 
                                                                   name="selectedPages" value="${fbPage.pageId}" 
                                                                   id="page_${fbPage.pageId}"
                                                                   ${fbPage['default'] ? 'checked' : ''}>
                                                            <div class="facebook-page-info">
                                                                <div class="facebook-page-name">${fbPage.pageName}</div>
                                                                <div class="facebook-page-id">ID: ${fbPage.pageId}</div>
                                                                <div class="facebook-badges">
                                                                    <c:if test="${fbPage['default']}">
                                                                        <span class="facebook-badge badge-primary">Default</span>
                                                                    </c:if>
                                                                    <c:if test="${!fbPage.autoPost}">
                                                                        <span class="facebook-badge badge-warning">Auto Post Off</span>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                            
                                            <div class="facebook-alert">
                                                <i class="fas fa-info-circle"></i>
                                                <div class="facebook-alert-content">
                                                    <strong>Lưu ý:</strong> Bài đăng chỉ được đăng lên Facebook khi được AI duyệt tự động (ACCEPT).
                                                    Nếu cần review thủ công, Facebook posting sẽ được thực hiện sau khi admin duyệt.
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>

                                <c:if test="${empty facebookPages}">
                                    <div class="facebook-empty">
                                        <i class="fab fa-facebook fa-3x"></i>
                                        <h6>Chưa cấu hình Facebook Pages</h6>
                                        <p>
                                            Để tự động đăng bài lên Facebook, vui lòng cấu hình Facebook Pages trước.
                                        </p>
                                        <a href="${pageContext.request.contextPath}/facebook/manage" class="btn btn-primary btn-sm">
                                            <i class="fas fa-cog"></i> Cấu hình Facebook
                                        </a>
                                    </div>
                                </c:if>

                                <!-- ✅ SCHEDULED POSTING SECTION -->
                                <div class="scheduled-section">
                                    <div class="scheduled-header">
                                        <h5><i class="fas fa-clock"></i> Lập lịch đăng bài</h5>
                                    </div>
                                    <div class="scheduled-body">
                                        <div class="scheduled-description">
                                            <p>Chọn thời gian để đăng bài tự động. Nếu không chọn, bài sẽ được xử lý ngay lập tức.</p>
                                        </div>
                                        
                                        <div class="form-group">
                                            <div class="scheduled-options">
                                                <label class="scheduled-option">
                                                    <input type="radio" name="publishType" value="now" checked onclick="toggleScheduled()">
                                                    <span class="option-content">
                                                        <i class="fas fa-bolt"></i>
                                                        <div class="option-text">
                                                            <strong>Đăng ngay</strong>
                                                            <small>Bài viết sẽ được xử lý và đăng ngay lập tức</small>
                                                        </div>
                                                    </span>
                                                </label>
                                                
                                                <label class="scheduled-option">
                                                    <input type="radio" name="publishType" value="scheduled" onclick="toggleScheduled()">
                                                    <span class="option-content">
                                                        <i class="fas fa-calendar-alt"></i>
                                                        <div class="option-text">
                                                            <strong>Lập lịch đăng</strong>
                                                            <small>Chọn thời gian cụ thể để đăng bài tự động</small>
                                                        </div>
                                                    </span>
                                                </label>
                                            </div>
                                        </div>
                                        
                                        <div class="scheduled-datetime" id="scheduledDateTime" style="display: none;">
                                            <div class="form-group">
                                                <label for="scheduledAt">
                                                    <i class="fas fa-clock"></i>
                                                    Thời gian đăng <span class="required">*</span>
                                                </label>
                                                <input type="datetime-local" 
                                                       class="form-control" 
                                                       id="scheduledAt" 
                                                       name="scheduledAt"
                                                       placeholder="YYYY-MM-DDTHH:MM"
                                                       step="60">
                                                <div class="form-help">
                                                    <small>
                                                        <i class="fas fa-info-circle"></i>
                                                        Thời gian phải trong tương lai (tối đa 30 ngày)
                                                    </small>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="scheduled-info" id="scheduledInfo" style="display: none;">
                                            <div class="info-card">
                                                <i class="fas fa-info-circle"></i>
                                                <div class="info-content">
                                                    <strong>Cách hoạt động của lịch đăng:</strong>
                                                    <ul>
                                                        <li>Bài viết sẽ được lưu với trạng thái "Đã lập lịch"</li>
                                                        <li>Hệ thống tự động kiểm tra và đăng bài khi đến giờ</li>
                                                        <li>Facebook Pages đã chọn sẽ được đăng tự động</li>
                                                        <li>Bạn có thể hủy hoặc chỉnh sửa lịch trước khi đăng</li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Action Buttons -->
                                <div class="form-actions">
                                    <button type="button" class="btn btn-secondary" onclick="history.back()">
                                        <i class="fas fa-times"></i>
                                        Hủy
                                    </button>
                                    <button type="submit" class="btn btn-primary" id="submitBtn">
                                        <i class="fas fa-paper-plane"></i>
                                        Đăng tin
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </main>
            </div>
        </div>

        <script>
            // Sidebar functionality
            document.addEventListener('DOMContentLoaded', function () {
                const sidebar = document.getElementById('sidebar');
                const sidebarToggle = document.getElementById('sidebarToggle');
                const mainContent = document.querySelector('.main-content');

                if (sidebarToggle) {
                    sidebarToggle.addEventListener('click', function () {
                        if (window.innerWidth <= 768) {
                            sidebar.classList.toggle('show');
                        } else {
                            sidebar.classList.toggle('collapsed');
                            mainContent.classList.toggle('expanded');
                        }
                    });
                }

                window.addEventListener('resize', function () {
                    if (window.innerWidth > 768) {
                        sidebar.classList.remove('show');
                    } else {
                        sidebar.classList.remove('collapsed');
                        mainContent.classList.remove('expanded');
                    }
                });

                document.addEventListener('click', function (event) {
                    if (window.innerWidth <= 768 &&
                            !sidebar.contains(event.target) &&
                            !sidebarToggle.contains(event.target) &&
                            sidebar.classList.contains('show')) {
                        sidebar.classList.remove('show');
                    }
                });

                // Auto dismiss alerts
                setTimeout(function () {
                    const alerts = document.querySelectorAll('.alert');
                    alerts.forEach(function (alert) {
                        alert.style.display = 'none';
                    });
                }, 5000);

                // ✅ Initialize selected pages count
                updateSelectedPagesCount();
            });

            // ✅ Update selected Facebook pages count
            function updateSelectedPagesCount() {
                const checkboxes = document.querySelectorAll('input[name="selectedPages"]:checked:not(:disabled)');
                const count = checkboxes.length;
                const info = document.getElementById('selectedPagesInfo');
                const countSpan = document.getElementById('selectedCount');
                
                if (info && countSpan) {
                    countSpan.textContent = count;
                    if (count > 0) {
                        info.classList.add('show');
                    } else {
                        info.classList.remove('show');
                    }
                }
            }

            // Image upload functionality
            const MAX_IMAGES = 5;

            function onImagesChange(input) {
                const info = document.getElementById('imagesInfo');
                const preview = document.getElementById('imagePreview');

                if (!input.files)
                    return;

                if (input.files.length > MAX_IMAGES) {
                    alert('Chỉ được chọn tối đa ' + MAX_IMAGES + ' ảnh.');
                    input.value = '';
                    info.innerHTML = '';
                    info.classList.remove('show');
                    preview.innerHTML = '';
                    return;
                }

                if (input.files.length > 0) {
                    info.innerHTML = `<div class="files-selected"><i class="fas fa-check-circle"></i> Đã chọn <strong>${input.files.length}</strong> ảnh</div>`;
                    info.classList.add('show');

                    preview.innerHTML = '';
                    Array.from(input.files).forEach((file, index) => {
                        if (file.type.startsWith('image/')) {
                            const reader = new FileReader();
                            reader.onload = function (e) {
                                const imgDiv = document.createElement('div');
                                imgDiv.className = 'preview-item';
                                imgDiv.innerHTML =
                                        '<img src="' + e.target.result + '" alt="Preview ' + (index + 1) + '">' +
                                        '<div class="preview-info">' +
                                        '<span class="file-name">' + file.name + '</span>' +
                                        '<span class="file-size">' + formatFileSize(file.size) + '</span>' +
                                        '</div>' +
                                        (index === 0 ? '<div class="primary-badge">Ảnh đại diện</div>' : '');
                                preview.appendChild(imgDiv);
                            };
                            reader.readAsDataURL(file);
                        }
                    });
                } else {
                    info.innerHTML = '';
                    info.classList.remove('show');
                    preview.innerHTML = '';
                }
            }

            function formatFileSize(bytes) {
                if (bytes === 0)
                    return '0 Bytes';
                const k = 1024;
                const sizes = ['Bytes', 'KB', 'MB', 'GB'];
                const i = Math.floor(Math.log(bytes) / Math.log(k));
                return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
            }

            // Drag and drop functionality
            function dropHandler(ev) {
                ev.preventDefault();
                const container = ev.currentTarget;
                container.classList.remove('dragover');

                const files = ev.dataTransfer.files;
                const input = document.getElementById('images');
                input.files = files;
                onImagesChange(input);
            }

            function dragOverHandler(ev) {
                ev.preventDefault();
                const container = ev.currentTarget;
                container.classList.add('dragover');
            }

            function dragLeaveHandler(ev) {
                const container = ev.currentTarget;
                container.classList.remove('dragover');
            }

            // Form submission
            document.getElementById('postForm').addEventListener('submit', function (e) {
                const title = document.getElementById('title').value.trim();
                if (!title) {
                    e.preventDefault();
                    alert('Vui lòng nhập tiêu đề tin đăng!');
                    document.getElementById('title').focus();
                    return;
                }

                // ✅ Validate scheduled posting
                if (!validateScheduledPosting()) {
                    e.preventDefault();
                    return;
                }

                // ✅ Validate Facebook pages selection (optional warning)
                const selectedPages = document.querySelectorAll('input[name="selectedPages"]:checked:not(:disabled)');
                if (selectedPages.length === 0) {
                    const confirmPost = confirm('Bạn chưa chọn Facebook Page nào. Tin đăng sẽ không được đăng lên Facebook. Bạn có muốn tiếp tục?');
                    if (!confirmPost) {
                        e.preventDefault();
                        return;
                    }
                }

                // Show loading state
                const submitBtn = document.getElementById('submitBtn');
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang đăng tin...';
                submitBtn.classList.add('btn-loading');
            });

            // Facebook Pages Selection Functions
            function togglePage(pageItem) {
                const checkbox = pageItem.querySelector('input[type="checkbox"]');
                const isChecked = checkbox.checked;
                
                // Toggle checkbox
                checkbox.checked = !isChecked;
                
                // Update visual state
                if (checkbox.checked) {
                    pageItem.classList.add('selected');
                } else {
                    pageItem.classList.remove('selected');
                }
                
                // Trigger change event for any listeners
                checkbox.dispatchEvent(new Event('change'));
            }

            // Initialize Facebook Pages selection on page load
            document.addEventListener('DOMContentLoaded', function() {
                // Set initial visual state for checkboxes
                const pageItems = document.querySelectorAll('.facebook-page-item');
                pageItems.forEach(function(item) {
                    const checkbox = item.querySelector('input[type="checkbox"]');
                    if (checkbox && checkbox.checked) {
                        item.classList.add('selected');
                    }
                    
                    // Prevent checkbox click from bubbling to parent
                    checkbox.addEventListener('click', function(e) {
                        e.stopPropagation();
                        
                        // Update visual state
                        if (this.checked) {
                            item.classList.add('selected');
                        } else {
                            item.classList.remove('selected');
                        }
                        
                        // Update count
                        updateSelectedPagesCount();
                    });
                });
                
                updateSelectedPagesCount();
                
                // ✅ Check browser support
                checkDateTimeSupport();
                
                // ✅ Initialize scheduled posting datetime
                initScheduledDateTime();
                
                // ✅ Initialize toggle state
                toggleScheduled();
                
                // ✅ Test datetime input immediately
                const datetimeInput = document.getElementById('scheduledAt');
                if (datetimeInput) {
                    console.log('Datetime input found:', {
                        type: datetimeInput.type,
                        min: datetimeInput.min,
                        max: datetimeInput.max,
                        value: datetimeInput.value
                    });
                } else {
                    console.error('Datetime input not found!');
                }
            });

            // ✅ SCHEDULED POSTING FUNCTIONS
            function toggleScheduled() {
                console.log('toggleScheduled() called');
                
                const publishType = document.querySelector('input[name="publishType"]:checked').value;
                const datetimeDiv = document.getElementById('scheduledDateTime');
                const infoDiv = document.getElementById('scheduledInfo');
                const datetimeInput = document.getElementById('scheduledAt');
                
                console.log('Publish type:', publishType);
                console.log('Elements found:', {
                    datetimeDiv: !!datetimeDiv,
                    infoDiv: !!infoDiv,
                    datetimeInput: !!datetimeInput
                });
                
                if (publishType === 'scheduled') {
                    if (datetimeDiv) datetimeDiv.style.display = 'block';
                    if (infoDiv) infoDiv.style.display = 'block';
                    if (datetimeInput) {
                        datetimeInput.required = true;
                        
                        // Re-initialize datetime constraints when showing
                        const now = new Date();
                        now.setMinutes(now.getMinutes() + 5);
                        const minDateTime = new Date(now.getTime() - (now.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);
                        datetimeInput.min = minDateTime;
                        
                        console.log('Datetime input enabled, min time:', minDateTime);
                    }
                } else {
                    if (datetimeDiv) datetimeDiv.style.display = 'none';
                    if (infoDiv) infoDiv.style.display = 'none';
                    if (datetimeInput) {
                        datetimeInput.required = false;
                        datetimeInput.value = '';
                    }
                }
            }
            
            function initScheduledDateTime() {
                // Set minimum datetime to current time + 5 minutes
                const now = new Date();
                now.setMinutes(now.getMinutes() + 5);
                
                // Fix timezone issue - use local timezone
                const minDateTime = new Date(now.getTime() - (now.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);
                
                // Set maximum datetime to 30 days from now
                const maxDate = new Date();
                maxDate.setDate(maxDate.getDate() + 30);
                const maxDateTime = new Date(maxDate.getTime() - (maxDate.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);
                
                const datetimeInput = document.getElementById('scheduledAt');
                if (datetimeInput) {
                    datetimeInput.min = minDateTime;
                    datetimeInput.max = maxDateTime;
                    
                    // Add debug logging
                    console.log('Datetime input initialized:', {
                        min: minDateTime,
                        max: maxDateTime,
                        element: datetimeInput
                    });
                }
            }
            
            // ✅ Check browser support for datetime-local
            function checkDateTimeSupport() {
                const input = document.createElement('input');
                input.type = 'datetime-local';
                const isSupported = input.type === 'datetime-local';
                
                console.log('Browser datetime-local support:', isSupported);
                
                if (!isSupported) {
                    console.warn('Browser does not support datetime-local input');
                    // Create fallback inputs for date and time separately
                    createDateTimeFallback();
                }
                
                return isSupported;
            }
            
            // ✅ Create fallback date/time inputs for older browsers
            function createDateTimeFallback() {
                const datetimeInput = document.getElementById('scheduledAt');
                if (!datetimeInput) return;
                
                // Create container for date and time inputs
                const container = document.createElement('div');
                container.className = 'datetime-fallback';
                container.style.display = 'flex';
                container.style.gap = '10px';
                
                // Create date input
                const dateInput = document.createElement('input');
                dateInput.type = 'date';
                dateInput.id = 'scheduledDate';
                dateInput.name = 'scheduledDate';
                dateInput.className = 'form-control';
                dateInput.style.flex = '1';
                
                // Create time input
                const timeInput = document.createElement('input');
                timeInput.type = 'time';
                timeInput.id = 'scheduledTime';
                timeInput.name = 'scheduledTime';
                timeInput.className = 'form-control';
                timeInput.style.flex = '1';
                
                // Set minimum date to today
                const today = new Date().toISOString().split('T')[0];
                dateInput.min = today;
                
                // Set maximum date to 30 days from now
                const maxDate = new Date();
                maxDate.setDate(maxDate.getDate() + 30);
                dateInput.max = maxDate.toISOString().split('T')[0];
                
                // Replace datetime-local input with fallback
                datetimeInput.parentNode.insertBefore(container, datetimeInput);
                container.appendChild(dateInput);
                container.appendChild(timeInput);
                datetimeInput.style.display = 'none';
                
                // Sync fallback inputs with original input
                function syncInputs() {
                    const date = dateInput.value;
                    const time = timeInput.value;
                    if (date && time) {
                        datetimeInput.value = date + 'T' + time;
                    } else {
                        datetimeInput.value = '';
                    }
                }
                
                dateInput.addEventListener('change', syncInputs);
                timeInput.addEventListener('change', syncInputs);
                
                console.log('Created datetime fallback inputs');
            }
            
            // ✅ Enhanced form validation for scheduled posting
            function validateScheduledPosting() {
                const publishType = document.querySelector('input[name="publishType"]:checked').value;
                
                if (publishType === 'scheduled') {
                    // Check browser support first
                    if (!checkDateTimeSupport()) {
                        return false;
                    }
                    
                    const scheduledAt = document.getElementById('scheduledAt').value;
                    console.log('Scheduled time value:', scheduledAt);
                    
                    if (!scheduledAt) {
                        alert('Vui lòng chọn thời gian đăng bài');
                        return false;
                    }
                    
                    const scheduledDate = new Date(scheduledAt);
                    const now = new Date();
                    const maxDate = new Date();
                    maxDate.setDate(maxDate.getDate() + 30);
                    
                    if (scheduledDate <= now) {
                        alert('Thời gian đăng phải trong tương lai');
                        return false;
                    }
                    
                    if (scheduledDate > maxDate) {
                        alert('Chỉ được lập lịch tối đa 30 ngày trước');
                        return false;
                    }
                }
                
                return true;
            }
        </script>
    </body>
</html>