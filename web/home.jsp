<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<html lang="vi">
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>PhongTro247 - Tìm phòng trọ dễ dàng</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            /* Reset & Base Styles */
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Inter', 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
                line-height: 1.6;
                color: #2d3748;
                background: #f8fafc;
                min-height: 100vh;
                margin: 0;
                padding: 0;
            }

            /* Header Styles - Professional */
            .header {
                background: #ffffff;
                color: #2d3748;
                padding: 1.2rem 0;
                box-shadow: 0 2px 20px rgba(0,0,0,0.08);
                position: sticky;
                top: 0;
                z-index: 1000;
                border-bottom: 1px solid #e2e8f0;
            }

            .header-content {
                max-width: 1800px;
                margin: 0 auto;
                padding: 0 2rem;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .logo {
                font-size: 2rem;
                font-weight: 800;
                display: flex;
                align-items: center;
                text-decoration: none;
                color: #667eea;
                transition: all 0.3s ease;
            }

            .logo:hover {
                color: #5a67d8;
                text-decoration: none;
                transform: translateY(-1px);
            }

            .logo i {
                margin-right: 0.8rem;
                color: #667eea;
                font-size: 2.2rem;
                background: linear-gradient(135deg, #667eea, #764ba2);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
            }

            @keyframes pulse {
                0%, 100% {
                    transform: scale(1);
                }
                50% {
                    transform: scale(1.1);
                }
            }

            .nav-links {
                display: flex;
                gap: 1rem;
                align-items: center;
            }

            .nav-links a {
                color: #4a5568;
                text-decoration: none;
                padding: 0.8rem 1.5rem;
                border-radius: 12px;
                transition: all 0.3s ease;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 0.5rem;
                position: relative;
                font-size: 0.95rem;
            }

            .nav-links a:hover {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
            }

            .nav-links a:hover::before {
                left: 100%;
            }

            .nav-toggle {
                display: none;
                background: none;
                border: none;
                color: white;
                font-size: 1.5rem;
                cursor: pointer;
                padding: 0.5rem;
            }

            /* Container */
            .container {
                max-width: 1800px;
                margin: 0 auto;
                padding: 0 1rem;
            }

            /* Main Layout - Horizontal Search + Map Below */
            .main-layout {
                display: flex;
                flex-direction: column;
                gap: 2rem;
                margin-top: 2rem;
                margin-bottom: 3rem;
                min-height: calc(100vh - 200px);
            }

            .search-panel {
                width: 100%;
                margin-bottom: 2rem;
            }

            .content-panel {
                background: white;
                border-radius: 20px;
                overflow: hidden;
                box-shadow: 0 10px 40px rgba(0,0,0,0.06);
                border: 1px solid #e2e8f0;
                display: flex;
                justify-content: center;
                align-items: flex-start;
                gap: 2rem;
                flex: 1;
                min-height: 600px;
                padding: 2rem;
                max-width: 1400px;
                margin: 0 auto;
            }

            /* Search Section - Professional */
            .search-section {
                background: white;
                border-radius: 20px;
                padding: 2rem;
                margin: 0;
                box-shadow:
                    0 10px 40px rgba(0,0,0,0.06),
                    0 4px 20px rgba(102, 126, 234, 0.08);
                border: 1px solid #e2e8f0;
                position: relative;
                overflow: hidden;
            }

            .search-section::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 4px;
                background: linear-gradient(90deg, #667eea, #764ba2, #48bb78, #667eea);
                animation: shimmer 4s infinite;
            }

            @keyframes shimmer {
                0% {
                    left: -100%;
                }
                100% {
                    left: 100%;
                }
            }

            .search-title {
                text-align: center;
                margin-bottom: 1.8rem;
                color: #2d3748;
                font-size: 1.8rem;
                font-weight: 700;
                position: relative;
            }

            .search-title i {
                color: #667eea;
                margin-right: 0.5rem;
            }

            .search-form {
                width: 100%;
            }

            .search-grid {
                display: flex;
                flex-direction: column;
                gap: 1.5rem;
            }

            .search-criteria {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 1rem;
            }

            .form-group {
                display: flex;
                flex-direction: column;
            }

            .form-group label {
                font-weight: 600;
                margin-bottom: 0.8rem;
                color: #374151;
                font-size: 0.95rem;
                display: block;
            }

            .form-group label i {
                color: #667eea;
                margin-right: 0.5rem;
            }

            .form-input {
                padding: 1rem 1.2rem;
                border: 2px solid #e5e7eb;
                border-radius: 12px;
                font-size: 1rem;
                transition: all 0.3s ease;
                background: white;
                font-family: inherit;
                width: 100%;
            }

            .form-input:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1), 0 4px 12px rgba(0,0,0,0.08);
                background: white;
                transform: translateY(-1px);
            }

            .form-input:hover {
                border-color: #cbd5e0;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
            }

            .price-range, .area-range {
                display: grid;
                grid-template-columns: 1fr auto 1fr;
                gap: 0.8rem;
                align-items: center;
            }

            .range-separator {
                color: #667eea;
                font-weight: 700;
                font-size: 1.2rem;
            }

            /* Range Slider */
            .range-container {
                display: flex;
                flex-direction: column;
                gap: 0.3rem;
            }

            .range-display {
                display: flex;
                justify-content: center;
            }

            .range-slider {
                width: 100%;
                height: 8px;
                border-radius: 4px;
                background: linear-gradient(to right, #e2e8f0 0%, #e2e8f0 100%);
                outline: none;
                -webkit-appearance: none;
                appearance: none;
                cursor: pointer;
            }

            .range-slider::-webkit-slider-thumb {
                -webkit-appearance: none;
                appearance: none;
                width: 24px;
                height: 24px;
                border-radius: 50%;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                cursor: pointer;
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                transition: all 0.2s ease;
            }

            .range-slider::-webkit-slider-thumb:hover {
                transform: scale(1.15);
                box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
            }

            .range-output {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 0.5rem 1rem;
                border-radius: 20px;
                font-size: 0.9rem;
                font-weight: 600;
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
            }

            /* Button Section - Horizontal */
            .search-buttons {
                display: flex;
                gap: 1rem;
                justify-content: center;
                margin-top: 1.5rem;
            }

            .btn {
                padding: 1.2rem 2.5rem;
                border: none;
                border-radius: 12px;
                font-size: 1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                gap: 0.8rem;
                text-decoration: none;
                position: relative;
                overflow: hidden;
                font-family: inherit;
            }

            .btn::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
                transition: left 0.5s;
            }

            .btn:hover::before {
                left: 100%;
            }

            .btn-primary {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
            }

            .btn-primary:hover {
                transform: translateY(-3px);
                box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
            }

            .btn-secondary {
                background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
                color: white;
                box-shadow: 0 6px 20px rgba(72, 187, 120, 0.3);
            }

            .btn-secondary:hover {
                transform: translateY(-3px);
                box-shadow: 0 10px 30px rgba(72, 187, 120, 0.4);
            }

            /* Alert Messages */
            .alert {
                padding: 1rem 1.5rem;
                border-radius: 12px;
                margin-bottom: 1.5rem;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .alert-error {
                background: linear-gradient(135deg, #fed7d7 0%, #feb2b2 100%);
                color: #c53030;
                border: 1px solid #feb2b2;
            }

            .alert-success {
                background: linear-gradient(135deg, #c6f6d5 0%, #9ae6b4 100%);
                color: #2f855a;
                border: 1px solid #9ae6b4;
            }

            /* Content Grid */
            .content-grid {
                display: flex;
                gap: 2rem;
                margin-top: 2rem;
                justify-content: center;
                align-items: flex-start;
            }

            .main-content {
                background: white;
                border-radius: 20px;
                overflow: hidden;
                box-shadow: 0 10px 40px rgba(0,0,0,0.1);
                flex: 2;
                max-width: 1000px;
                min-width: 700px;
            }

            .map-container {
                height: 700px;
                position: relative;
                flex-shrink: 0;
                border-bottom: 1px solid #e2e8f0;
            }

            #map {
                width: 100%;
                height: 100%;
            }

            .results-section {
                padding: 2rem;
                background: #f8fafc;
            }

            .results-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 2rem;
                padding-bottom: 1rem;
                border-bottom: 2px solid #e2e8f0;
            }

            .results-count {
                font-size: 1.2rem;
                font-weight: 700;
                color: #2d3748;
            }

            /* Post Cards - Modern Design */
            .post-card {
                border: 1px solid #e5e7eb;
                border-radius: 16px;
                margin-bottom: 1.5rem;
                overflow: hidden;
                cursor: pointer;
                transition: all 0.3s ease;
                background: white;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            }

            .post-card:hover {
                transform: translateY(-4px);
                box-shadow: 0 20px 50px rgba(0,0,0,0.12);
                border-color: #667eea;
            }

            .post-card-content {
                display: flex;
                padding: 1.5rem;
            }

            .post-thumb {
                flex-shrink: 0;
                width: 160px;
                height: 120px;
                border-radius: 12px;
                overflow: hidden;
                margin-right: 1.5rem;
            }

            .post-thumb img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                transition: transform 0.3s ease;
            }

            .post-card:hover .post-thumb img {
                transform: scale(1.05);
            }

            .no-image {
                width: 100%;
                height: 100%;
                background: linear-gradient(45deg, #f7fafc, #edf2f7);
                display: flex;
                align-items: center;
                justify-content: center;
                color: #a0aec0;
                font-size: 1rem;
            }

            .post-info {
                flex: 1;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
            }

            .post-title {
                font-size: 1.2rem;
                font-weight: 700;
                color: #2d3748;
                margin-bottom: 0.8rem;
                line-height: 1.4;
            }

            .post-title a {
                color: inherit;
                text-decoration: none;
                transition: color 0.3s ease;
            }

            .post-title a:hover {
                color: #667eea;
            }

            .post-address {
                color: #6b7280;
                font-size: 0.95rem;
                margin-bottom: 1rem;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .post-details {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
                margin-bottom: 1rem;
            }

            .detail-item {
                display: flex;
                align-items: center;
                gap: 0.3rem;
                font-size: 0.9rem;
                color: #4a5568;
            }

            .detail-item i {
                color: #667eea;
            }

            .post-meta {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 1rem;
            }

            .price {
                font-size: 1.3rem;
                font-weight: 700;
                color: #e53e3e;
            }

            .post-actions {
                display: flex;
                gap: 0.5rem;
                align-items: center;
            }

            .btn-detail {
                padding: 0.5rem 1rem;
                border-radius: 8px;
                font-size: 0.9rem;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                cursor: pointer;
                box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
            }

            .btn-detail:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                color: white;
                text-decoration: none;
            }

            .btn-detail i {
                font-size: 0.8rem;
            }

            .distance {
                font-size: 0.9rem;
                color: #48bb78;
                font-weight: 600;
            }

            /* Sidebar */
            .sidebar {
                display: flex;
                flex-direction: column;
                gap: 2rem;
                flex: 1;
                max-width: 350px;
                min-width: 300px;
            }

            .sidebar-card {
                background: white;
                border-radius: 20px;
                padding: 2rem;
                box-shadow: 0 10px 40px rgba(0,0,0,0.08);
            }

            .sidebar-title {
                font-size: 1.3rem;
                font-weight: 700;
                color: #2d3748;
                margin-bottom: 1.5rem;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .sidebar-title i {
                color: #667eea;
            }

            .stats-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 1rem;
            }

            .stat-item {
                text-align: center;
                padding: 1.5rem;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                border-radius: 15px;
                color: white;
                transition: transform 0.3s ease;
            }

            .stat-item:hover {
                transform: translateY(-3px);
            }

            .stat-number {
                font-size: 1.8rem;
                font-weight: bold;
            }

            .stat-label {
                font-size: 0.9rem;
                opacity: 0.9;
            }

            /* Pagination */
            .pagination {
                display: flex;
                justify-content: center;
                gap: 0.5rem;
                margin-top: 2rem;
                padding-top: 2rem;
                border-top: 2px solid #e2e8f0;
            }

            /* Welcome Section */
            .welcome-section {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 3rem;
                padding: 3rem 2rem;
                min-height: 600px;
                width: 100%;
            }

            .welcome-content {
                text-align: center;
                max-width: 800px;
            }

            .welcome-icon {
                margin-bottom: 2rem;
            }

            .welcome-icon i {
                font-size: 5rem;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                animation: pulse 2s ease-in-out infinite alternate;
            }

            .welcome-title {
                font-size: 2.5rem;
                font-weight: 700;
                color: #2d3748;
                margin-bottom: 1.5rem;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
            }

            .welcome-description {
                font-size: 1.2rem;
                color: #6b7280;
                line-height: 1.6;
                margin-bottom: 3rem;
            }

            .welcome-features {
                display: grid;
                grid-template-columns: repeat(4, 1fr);

                gap: 2rem;
                margin-bottom: 2rem;
            }

            .feature-item {
                display: flex;
                flex-direction: column;
                align-items: center;
                padding: 2rem;
                background: white;
                border-radius: 15px;
                box-shadow: 0 8px 25px rgba(0,0,0,0.08);
                transition: all 0.3s ease;
                border: 1px solid #e2e8f0;
            }

            .feature-item:hover {
                transform: translateY(-5px);
                box-shadow: 0 15px 40px rgba(102, 126, 234, 0.15);
                border-color: #667eea;
            }

            .feature-item i {
                font-size: 2.5rem;
                color: #667eea;
                margin-bottom: 1rem;
            }

            .feature-item span {
                font-weight: 600;
                color: #2d3748;
                font-size: 1rem;
            }

            .default-map-container {
                width: 100%;
                max-width: 1200px;
                height: 500px;
                border-radius: 20px;
                overflow: hidden;
                box-shadow: 0 15px 50px rgba(0,0,0,0.1);
                border: 1px solid #e2e8f0;
            }

            .default-map-container #map {
                width: 100%;
                height: 100%;
            }
            .pagination a, .pagination b {
                padding: 0.8rem 1.2rem;
                border-radius: 10px;
                text-decoration: none;
                font-weight: 600;
                transition: all 0.3s ease;
            }

            .pagination a {
                background: white;
                color: #667eea;
                border: 2px solid #e2e8f0;
            }

            .pagination a:hover {
                background: #667eea;
                color: white;
                border-color: #667eea;
                transform: translateY(-2px);
            }

            .pagination b {
                background: #667eea;
                color: white;
                border: 2px solid #667eea;
            }

            /* Footer */
            .footer {
                background: linear-gradient(135deg, #1a202c 0%, #2d3748 50%, #4a5568 100%);
                color: white;
                padding: 4rem 0 2rem 0;
                margin-top: 4rem;
                position: relative;
            }

            .footer::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 1px;
                background: linear-gradient(90deg, transparent, #ffd700, transparent);
            }

            .footer-content {
                max-width: 1400px;
                margin: 0 auto;
                padding: 0 2rem;
                display: grid;
                grid-template-columns: 1.2fr 0.8fr 0.8fr 1fr;
                gap: 3rem;
                align-items: start;
            }

            .footer-section {
                display: flex;
                flex-direction: column;
            }

            .footer-section h4 {
                font-size: 1.3rem;
                font-weight: 700;
                margin-bottom: 1.5rem;
                color: #ffd700;
                position: relative;
                padding-bottom: 0.5rem;
            }

            .footer-section h4::after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 0;
                width: 30px;
                height: 2px;
                background: #ffd700;
            }

            /* About Section */
            .footer-about {
                padding-right: 1rem;
            }

            .footer-about .brand {
                font-size: 1.8rem;
                font-weight: 800;
                margin-bottom: 1rem;
                color: #ffd700;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .footer-about p {
                color: #cbd5e0;
                line-height: 1.7;
                margin-bottom: 1.5rem;
                font-size: 0.95rem;
            }

            .footer-about .stats {
                display: flex;
                gap: 2rem;
                margin-top: 1.5rem;
            }

            .stat-item {
                text-align: center;
            }

            .stat-number {
                font-size: 1.5rem;
                font-weight: 700;
                color: #ffd700;
                display: block;
            }

            .stat-label {
                font-size: 0.8rem;
                color: #a0aec0;
            }

            /* Links Section */
            .footer-links {
                display: flex;
                flex-direction: column;
                gap: 0.8rem;
            }

            .footer-link {
                color: #cbd5e0;
                text-decoration: none;
                font-size: 0.95rem;
                padding: 0.5rem 0;
                transition: all 0.3s ease;
                position: relative;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .footer-link:hover {
                color: #ffd700;
                padding-left: 0.5rem;
            }

            .footer-link i {
                width: 16px;
                font-size: 0.9rem;
            }

            /* Services Section */
            .footer-services {
                display: flex;
                flex-direction: column;
                gap: 0.8rem;
            }

            .service-item {
                color: #cbd5e0;
                font-size: 0.95rem;
                padding: 0.5rem 0;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .service-item i {
                color: #ffd700;
                width: 16px;
            }

            /* Contact Section */
            .footer-contact {
                display: flex;
                flex-direction: column;
                gap: 1rem;
            }

            .contact-item {
                display: flex;
                align-items: flex-start;
                gap: 0.8rem;
                color: #cbd5e0;
                font-size: 0.95rem;
                margin-bottom: 0.8rem;
            }

            .contact-item i {
                color: #ffd700;
                width: 18px;
                margin-top: 0.2rem;
                flex-shrink: 0;
            }

            .social-links {
                display: flex;
                gap: 0.8rem;
                margin-top: 1rem;
            }

            .social-links a {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 40px;
                height: 40px;
                background: rgba(255, 215, 0, 0.1);
                border: 1px solid rgba(255, 215, 0, 0.2);
                border-radius: 50%;
                transition: all 0.3s ease;
                color: #cbd5e0;
            }

            .social-links a:hover {
                background: #ffd700;
                color: #1a202c;
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(255, 215, 0, 0.3);
            }

            .footer-bottom {
                text-align: center;
                padding: 2rem 0 1rem;
                margin-top: 3rem;
                border-top: 1px solid rgba(255, 255, 255, 0.1);
                color: #a0aec0;
                font-size: 0.9rem;
            }

            .footer-bottom p {
                margin: 0;
            }

            /* Footer Responsive */
            @media (max-width: 1024px) {
                .footer-content {
                    grid-template-columns: 1fr 1fr;
                    gap: 2.5rem;
                }
            }

            @media (max-width: 768px) {
                .footer-content {
                    grid-template-columns: 1fr;
                    gap: 2rem;
                    text-align: left;
                }

                .footer {
                    padding: 3rem 0 2rem 0;
                }

                .footer-about .stats {
                    justify-content: center;
                    gap: 1.5rem;
                }

                .social-links {
                    justify-content: center;
                }
            }

            @media (max-width: 480px) {
                .footer-content {
                    padding: 0 1rem;
                }

                .footer-about .stats {
                    flex-direction: column;
                    gap: 1rem;
                }
            }

            /* Responsive Design */
            @media (max-width: 1200px) {
                .container {
                    padding: 0 20px;
                }

                .search-criteria {
                    grid-template-columns: repeat(2, 1fr);
                    gap: 20px;
                }
            }

            @media (max-width: 768px) {
                .search-criteria {
                    grid-template-columns: 1fr;
                    gap: 15px;
                }

                .search-buttons {
                    flex-direction: column;
                    gap: 12px;
                }

                .content-panel {
                    flex-direction: column;
                    min-height: 400px;
                    padding: 1rem;
                }

                .welcome-section {
                    padding: 2rem 1rem;
                    gap: 2rem;
                }

                .welcome-title {
                    font-size: 2rem;
                }

                .welcome-description {
                    font-size: 1.1rem;
                }

                .welcome-features {
                    grid-template-columns: 1fr;
                    gap: 1.5rem;
                }

                .feature-item {
                    padding: 1.5rem;
                }

                .default-map-container {
                    height: 350px;
                }

                .header-content {
                    flex-direction: column;
                    gap: 20px;
                    text-align: center;
                }

                .nav-links {
                    flex-direction: column;
                    gap: 10px;
                }
            }
            @media (max-width: 768px) {
                .nav-toggle {
                    display: block;
                }

                .nav-links {
                    display: none;
                    position: absolute;
                    top: 100%;
                    left: 0;
                    right: 0;
                    background: white;
                    flex-direction: column;
                    padding: 1rem;
                    box-shadow: 0 8px 32px rgba(0,0,0,0.1);
                    border-top: 1px solid #e2e8f0;
                }

                .nav-links.active {
                    display: flex;
                }

                .header-content {
                    position: relative;
                    padding: 0 1rem;
                }

                .main-layout {
                    grid-template-columns: 1fr;
                    gap: 1.5rem;
                    margin-top: 1rem;
                }

                .search-section {
                    padding: 1.5rem;
                    margin: 0;
                }

                .search-title {
                    font-size: 1.5rem;
                    margin-bottom: 1.5rem;
                }

                .search-criteria {
                    grid-template-columns: 1fr;
                    gap: 1.2rem;
                }

                .search-buttons {
                    flex-direction: column;
                    gap: 0.8rem;
                }

                .btn {
                    width: 100%;
                }

                .price-range, .area-range {
                    grid-template-columns: 1fr;
                    gap: 0.8rem;
                }

                .range-separator {
                    text-align: center;
                }

                .content-panel {
                    padding: 1rem;
                    max-width: 100%;
                }

                .content-grid {
                    flex-direction: column;
                    gap: 1.5rem;
                }

                .sidebar {
                    order: -1;
                }

                .post-card-content {
                    flex-direction: column;
                }

                .post-thumb {
                    width: 100%;
                    height: 200px;
                    margin-right: 0;
                    margin-bottom: 1rem;
                }

                .post-details {
                    flex-direction: column;
                    gap: 0.5rem;
                }

                .stats-grid {
                    grid-template-columns: 1fr;
                }

                .search-grid {
                    grid-template-columns: 1fr;
                    gap: 1.5rem;
                }

                .search-criteria {
                    display: flex;
                    flex-direction: column;
                    gap: 1.5rem;
                }

                .search-buttons {
                    flex-direction: column;
                    gap: 1rem;
                    position: static;
                }

                .btn {
                    width: 100%;
                }
            }

            @media (max-width: 480px) {
                .search-section {
                    padding: 0.8rem;
                    margin: 0.3rem 0;
                }

                .search-title {
                    font-size: 1rem;
                    margin-bottom: 0.6rem;
                }

                .form-input {
                    padding: 0.4rem 0.6rem;
                    font-size: 0.8rem;
                }

                .btn {
                    padding: 0.6rem 1rem;
                    font-size: 0.8rem;
                }

                .map-container {
                    height: 350px;
                }
            }
        </style>
    </head>
    <body>
        <!-- Header -->
        <header class="header">
            <div class="header-content">
                <a href="/PhongTroNew/" class="logo">
                    <i class="fas fa-home"></i>
                    PhongTro247
                </a>
                <button class="nav-toggle" id="navToggle">
                    <i class="fas fa-bars"></i>
                </button>
                <nav class="nav-links" id="navLinks">
                    <a href="/PhongTroNew/">
                        <i class="fas fa-search"></i> Tìm phòng
                    </a>
                    <c:if test="${sessionScope.user == null}">
                        <a href="/PhongTroNew/login">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập
                        </a>
                        <a href="/PhongTroNew/register">
                            <i class="fas fa-user-plus"></i> Đăng ký
                        </a>
                    </c:if>
                    <c:if test="${sessionScope.user != null}">
                        <a href="/PhongTroNew/post/my">
                            <i class="fas fa-list"></i> Tin của tôi
                        </a>
                        <a href="/PhongTroNew/post/create">
                            <i class="fas fa-plus"></i> Đăng tin
                        </a>
                        <a href="/PhongTroNew/logout">
                            <i class="fas fa-sign-out-alt"></i> Đăng xuất
                        </a>
                    </c:if>
                </nav>
            </div>
        </header>

        <div class="container">
            <!-- Main Layout -->
            <div class="main-layout">
                <!-- Search Panel: Full Width -->
                <div class="search-panel">
                    <div class="search-section">
                        <h2 class="search-title">
                            <i class="fas fa-search"></i>
                            Tìm phòng trọ theo ý muốn
                        </h2>

                        <form action="${pageContext.request.contextPath}/search" method="get" class="search-form">
                            <div class="search-grid">
                                <div class="search-criteria">
                                    <div class="form-group">
                                        <label for="addressInput">
                                            <i class="fas fa-map-marker-alt"></i> Từ khóa hoặc địa chỉ
                                        </label>
                                        <input type="text" id="addressInput" name="q" class="form-input"
                                               placeholder="Nhập địa chỉ hoặc từ khóa..." value="${fn:escapeXml(param.q)}"/>
                                        <input type="hidden" id="lat" name="lat" value="${lat}"/>
                                        <input type="hidden" id="lng" name="lng" value="${lng}"/>
                                    </div>

                                    <div class="form-group">
                                        <label><i class="fas fa-dollar-sign"></i> Giá thuê (VNĐ)</label>
                                        <div class="price-range">
                                            <input type="number" name="minPrice" class="form-input"
                                                   placeholder="Từ" value="${param.minPrice}"/>
                                            <span class="range-separator">→</span>
                                            <input type="number" name="maxPrice" class="form-input"
                                                   placeholder="Đến" value="${param.maxPrice}"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label><i class="fas fa-expand-arrows-alt"></i> Diện tích (m²)</label>
                                        <div class="area-range">
                                            <input type="number" name="minArea" class="form-input"
                                                   placeholder="Từ" value="${param.minArea}"/>
                                            <span class="range-separator">→</span>
                                            <input type="number" name="maxArea" class="form-input"
                                                   placeholder="Đến" value="${param.maxArea}"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label><i class="fas fa-bullseye"></i> Bán kính tìm kiếm</label>
                                        <div class="range-container">
                                            <div class="range-display">
                                                <output id="radiusOutput" class="range-output">${radiusKm != null ? radiusKm : 2} km</output>
                                            </div>
                                            <input type="range" id="radiusSlider" class="range-slider"
                                                   min="0.2" max="20" step="0.1"
                                                   value="${radiusKm != null ? radiusKm : 2}"/>
                                            <input type="hidden" id="radiusKm" name="radiusKm"
                                                   value="${radiusKm != null ? radiusKm : 2}"/>
                                        </div>
                                    </div>
                                </div>

                                <div class="search-buttons">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-search"></i> Tìm kiếm ngay
                                    </button>
                                    <button type="button" id="locBtn" class="btn btn-secondary">
                                        <i class="fas fa-location-arrow"></i> Vị trí hiện tại
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Content Panel: Map & Results -->
                <div class="content-panel">

                    <!-- Alerts -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-error">
                            <i class="fas fa-exclamation-triangle"></i> ${error}
                        </div>
                    </c:if>
                    <c:if test="${not empty sessionScope.flash}">
                        <div class="alert alert-success">
                            <i class="fas fa-check-circle"></i> ${sessionScope.flash}
                        </div>
                        <c:remove var="flash" scope="session"/>
                    </c:if>

                    <!-- Check if there's a search query -->
                    <c:set var="hasSearch" value="${isSearch != null ? isSearch : hasSearch != null ? hasSearch : false}" />

                    <!-- Welcome Message when no search -->
                    <c:if test="${!hasSearch}">
                        <div class="welcome-section">
                            <div class="welcome-content">
                                <div class="welcome-icon">
                                    <i class="fas fa-home"></i>
                                </div>
                                <h2 class="welcome-title">Chào mừng đến với PhongTro247!</h2>
                                <p class="welcome-description">
                                    Nền tảng tìm kiếm phòng trọ hàng đầu Việt Nam. Hãy sử dụng form tìm kiếm ở trên để tìm căn phòng ưng ý.
                                </p>
                                <div class="welcome-features">
                                    <div class="feature-item">
                                        <i class="fas fa-search"></i>
                                        <span>Tìm kiếm thông minh</span>
                                    </div>
                                    <div class="feature-item">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <span>Định vị chính xác</span>
                                    </div>
                                    <div class="feature-item">
                                        <i class="fas fa-dollar-sign"></i>
                                        <span>Giá cả minh bạch</span>
                                    </div>
                                    <div class="feature-item">
                                        <i class="fas fa-shield-alt"></i>
                                        <span>Tin cậy & An toàn</span>
                                    </div>
                                </div>
                            </div>

                            <!-- Default Map -->
                            <div class="default-map-container">
                                <div id="map"></div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Content Grid - Only show when there's search -->
                    <c:if test="${hasSearch}">                    <!-- Content Grid -->
                        <div class="content-grid">
                            <!-- Main Content -->
                            <div class="main-content">
                                <!-- Map -->
                                <div class="map-container">
                                    <div id="map"></div>
                                </div>

                                <!-- Results -->
                                <div class="results-section">
                                    <div class="results-header">
                                        <h3 class="results-count">
                                            <i class="fas fa-list"></i> 
                                            Tìm thấy ${fn:length(posts)} phòng trọ
                                        </h3>
                                    </div>

                                    <c:forEach var="p" items="${posts}" varStatus="status">
                                        <div class="post-card" data-marker-idx="${status.index}">
                                            <div class="post-card-content">
                                                <div class="post-thumb">
                                                    <c:choose>
                                                        <c:when test="${not empty p.thumbnail}">
                                                            <img src="${pageContext.request.contextPath}${p.thumbnail}" 
                                                                 alt="Hình ảnh phòng trọ" 
                                                                 onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                                                            <div class="no-image" style="display:none;">
                                                                <i class="fas fa-image fa-2x"></i>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="no-image">
                                                                <i class="fas fa-image fa-2x"></i>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <div class="post-info">
                                                    <h4 class="post-title">
                                                        <a href="${pageContext.request.contextPath}/post/detail?id=${p.postId}">
                                                            ${fn:escapeXml(p.title)}
                                                        </a>
                                                    </h4>

                                                    <div class="post-address">
                                                        <i class="fas fa-map-marker-alt"></i>
                                                        ${fn:escapeXml(p.address)}
                                                    </div>

                                                    <div class="post-details">
                                                        <div class="detail-item">
                                                            <i class="fas fa-expand-arrows-alt"></i>
                                                            ${p.area} m²
                                                        </div>
                                                        <div class="detail-item">
                                                            <i class="fas fa-tag"></i>
                                                            ${p.statusName}
                                                        </div>
                                                        <c:if test="${not empty p.distanceKm}">
                                                            <div class="detail-item distance">
                                                                <i class="fas fa-route"></i>
                                                                <c:choose>
                                                                    <c:when test="${p.distanceKm != null and p.distanceKm lt 0.1}">
                                                                        &lt;100m
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <fmt:formatNumber value="${p.distanceKm}" pattern="#0.##"/> km
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </c:if>
                                                    </div>

                                                    <div class="post-meta">
                                                        <div class="price">
                                                            <fmt:formatNumber value="${p.price}" type="currency" currencySymbol="₫"/>
                                                        </div>
                                                        <div class="post-actions">
                                                            <a href="${pageContext.request.contextPath}/post/detail?id=${p.postId}" 
                                                               class="btn btn-detail">
                                                                <i class="fas fa-eye"></i>
                                                                Chi tiết
                                                            </a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>

                                    <!-- Pagination -->
                                    <c:if test="${totalPages > 1}">
                                        <div class="pagination">
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <c:choose>
                                                    <c:when test="${i == page}">
                                                        <b>${i}</b>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${pageContext.request.contextPath}/search?page=${i}&pageSize=${pageSize}<c:if test='${not empty param.q}'>&q=${fn:escapeXml(param.q)}</c:if><c:if test='${not empty param.minPrice}'>&minPrice=${param.minPrice}</c:if><c:if test='${not empty param.maxPrice}'>&maxPrice=${param.maxPrice}</c:if><c:if test='${not empty param.minArea}'>&minArea=${param.minArea}</c:if><c:if test='${not empty param.maxArea}'>&maxArea=${param.maxArea}</c:if><c:if test='${not empty param.lat}'>&lat=${param.lat}</c:if><c:if test='${not empty param.lng}'>&lng=${param.lng}</c:if><c:if test='${not empty param.radiusKm}'>&radiusKm=${param.radiusKm}</c:if>">${i}</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <!-- Sidebar -->
                            <div class="sidebar">
                                <div class="sidebar-card">
                                    <h3 class="sidebar-title">
                                        <i class="fas fa-chart-bar"></i> Thống kê
                                    </h3>
                                    <div class="stats-grid">
                                        <div class="stat-item">
                                            <div class="stat-number">${fn:length(posts)}</div>
                                            <div class="stat-label">Phòng tìm thấy</div>
                                        </div>
                                        <div class="stat-item">
                                            <div class="stat-number">${totalPages}</div>
                                            <div class="stat-label">Trang kết quả</div>
                                        </div>
                                    </div>
                                </div>

                                <div class="sidebar-card">
                                    <h3 class="sidebar-title">
                                        <i class="fas fa-info-circle"></i> Hướng dẫn
                                    </h3>
                                    <div style="color: #6b7280; font-size: 0.95rem; line-height: 1.6;">
                                        <p><strong>💡 Mẹo tìm kiếm:</strong></p>
                                        <ul style="margin-left: 1rem; margin-top: 0.8rem;">
                                            <li>Nhập địa chỉ cụ thể để tìm phòng gần bạn</li>
                                            <li>Sử dụng vị trí hiện tại để tìm nhanh hơn</li>
                                            <li>Điều chỉnh bán kính tìm kiếm phù hợp</li>
                                            <li>Click vào danh sách để xem vị trí trên bản đồ</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>

            <!-- Footer -->
            <footer class="footer">
                <div class="container">
                    <div class="footer-content">
                        <!-- About Us Section -->
                        <div class="footer-section footer-about">
                            <div class="brand">
                                <i class="fas fa-home"></i> PhongTro247
                            </div>
                            <p>Nền tảng tìm kiếm phòng trọ hàng đầu Việt Nam. Chúng tôi kết nối người tìm phòng với chủ nhà một cách nhanh chóng, an toàn và minh bạch.</p>

                            <div class="stats">
                                <div class="stat-item">
                                    <span class="stat-number">10K+</span>
                                    <span class="stat-label">Phòng trọ</span>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-number">5K+</span>
                                    <span class="stat-label">Khách hàng</span>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-number">100+</span>
                                    <span class="stat-label">Quận/Huyện</span>
                                </div>
                            </div>
                        </div>

                        <!-- Quick Links Section -->
                        <div class="footer-section">
                            <h4>Liên kết nhanh</h4>
                            <div class="footer-links">
                                <a href="${pageContext.request.contextPath}/" class="footer-link">
                                    <i class="fas fa-search"></i>
                                    Tìm phòng trọ
                                </a>
                                <a href="${pageContext.request.contextPath}/post/create" class="footer-link">
                                    <i class="fas fa-plus"></i>
                                    Đăng tin cho thuê
                                </a>
                                <a href="${pageContext.request.contextPath}/my-posts" class="footer-link">
                                    <i class="fas fa-list"></i>
                                    Quản lý tin đăng
                                </a>
                                <a href="${pageContext.request.contextPath}/about" class="footer-link">
                                    <i class="fas fa-info-circle"></i>
                                    Về chúng tôi
                                </a>
                                <a href="${pageContext.request.contextPath}/terms" class="footer-link">
                                    <i class="fas fa-file-contract"></i>
                                    Điều khoản sử dụng
                                </a>
                            </div>
                        </div>

                        <!-- Services Section -->
                        <div class="footer-section">
                            <h4>Dịch vụ</h4>
                            <div class="footer-services">
                                <div class="service-item">
                                    <i class="fas fa-home"></i>
                                    Cho thuê phòng trọ
                                </div>
                                <div class="service-item">
                                    <i class="fas fa-building"></i>
                                    Cho thuê căn hộ
                                </div>
                                <div class="service-item">
                                    <i class="fas fa-house-user"></i>
                                    Cho thuê nhà nguyên căn
                                </div>
                                <div class="service-item">
                                    <i class="fas fa-bed"></i>
                                    Tìm người ở ghép
                                </div>
                                <div class="service-item">
                                    <i class="fas fa-headset"></i>
                                    Hỗ trợ 24/7
                                </div>
                            </div>
                        </div>

                        <!-- Contact Section -->
                        <div class="footer-section footer-contact">
                            <h4>Liên hệ</h4>

                            <div class="contact-item">
                                <i class="fas fa-envelope"></i>
                                <div>
                                    <strong>Email:</strong><br>
                                    support@phongtro247.com
                                </div>
                            </div>

                            <div class="contact-item">
                                <i class="fas fa-phone"></i>
                                <div>
                                    <strong>Hotline:</strong><br>
                                    1900 1234 (8:00 - 22:00)
                                </div>
                            </div>

                            <div class="contact-item">
                                <i class="fas fa-map-marker-alt"></i>
                                <div>
                                    <strong>Địa chỉ:</strong><br>
                                    TP.HCM, Việt Nam
                                </div>
                            </div>

                            <div class="social-links">
                                <a href="#" title="Facebook" aria-label="Facebook">
                                    <i class="fab fa-facebook-f"></i>
                                </a>
                                <a href="#" title="Zalo" aria-label="Zalo">
                                    <i class="fas fa-comment-dots"></i>
                                </a>
                                <a href="#" title="YouTube" aria-label="YouTube">
                                    <i class="fab fa-youtube"></i>
                                </a>
                                <a href="#" title="Instagram" aria-label="Instagram">
                                    <i class="fab fa-instagram"></i>
                                </a>
                                <a href="#" title="TikTok" aria-label="TikTok">
                                    <i class="fab fa-tiktok"></i>
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="footer-bottom">
                        <p>&copy; 2024 PhongTro247. Tất cả quyền được bảo lưu. | Made with ❤️ in Vietnam | Thiết kế bởi PhongTro247 Team</p>
                    </div>
                </div>
            </footer>

            <!-- JavaScript -->
            <script>
                // Mobile Navigation Toggle
                document.getElementById('navToggle').addEventListener('click', function () {
                    const navLinks = document.getElementById('navLinks');
                    navLinks.classList.toggle('active');

                    const icon = this.querySelector('i');
                    if (navLinks.classList.contains('active')) {
                        icon.classList.remove('fa-bars');
                        icon.classList.add('fa-times');
                    } else {
                        icon.classList.remove('fa-times');
                        icon.classList.add('fa-bars');
                    }
                });

                // Range slider functionality
                document.getElementById('radiusSlider').addEventListener('input', function () {
                    var value = this.value;
                    document.getElementById('radiusKm').value = value;
                    document.getElementById('radiusOutput').textContent = value + ' km';
                });

                // Build markers array from server-side posts
                var markers = [];
                <c:forEach var="p" items="${posts}" varStatus="status">
                    <c:if test="${not empty p.lat && not empty p.lng}">
                        markers.push({
                            id: ${p.postId},
                            title: '${fn:escapeXml(p.title)}',
                            lat: ${p.lat},
                            lng: ${p.lng},
                            thumb: '${pageContext.request.contextPath}${p.thumbnail}',
                            url: '${pageContext.request.contextPath}/post/detail?id=${p.postId}',
                            price: '${p.price}',
                            area: '${p.area}'
                        });
                    </c:if>
                </c:forEach>
            </script>

            <script src="https://unpkg.com/@googlemaps/markerclusterer/dist/index.min.js"></script>
            <script>
                            var map, markerObjs = [], circle = null;

                            function initMap() {
                                var defaultCenter = {lat: 10.762622, lng: 106.660172}; // HCM City
                                if (markers.length > 0) {
                                    defaultCenter = {lat: markers[0].lat, lng: markers[0].lng};
                                }
                                var searchLat = parseFloat(document.getElementById('lat').value || "NaN");
                                var searchLng = parseFloat(document.getElementById('lng').value || "NaN");
                                if (!isNaN(searchLat) && !isNaN(searchLng)) {
                                    defaultCenter = {lat: searchLat, lng: searchLng};
                                }

                                map = new google.maps.Map(document.getElementById('map'), {
                                    center: defaultCenter,
                                    zoom: 13,
                                    styles: [
                                        {
                                            featureType: "poi",
                                            elementType: "labels",
                                            stylers: [{visibility: "off"}]
                                        }
                                    ]
                                });

                                // Create custom marker icon
                                var markerIcon = {
                                    url: 'data:image/svg+xml;base64,' + btoa('<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#667eea" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>'),
                                    scaledSize: new google.maps.Size(32, 32),
                                    anchor: new google.maps.Point(16, 32)
                                };

                                // Draw markers and clustering
                                markerObjs = [];
                                markers.forEach(function (m, idx) {
                                    var marker = new google.maps.Marker({
                                        position: {lat: parseFloat(m.lat), lng: parseFloat(m.lng)},
                                        map: map,
                                        title: m.title,
                                        icon: markerIcon
                                    });

                                    var infoContent = `
                            <div style="max-width: 280px; font-family: 'Segoe UI', sans-serif;">
                                <img src="${m.thumb}" style="width: 100%; height: 150px; object-fit: cover; border-radius: 8px; margin-bottom: 8px;" onerror="this.style.display='none'"/>
                                <h4 style="margin: 0 0 8px 0; color: #2d3748; font-size: 16px;">${m.title}</h4>
                                <p style="margin: 0 0 4px 0; color: #4a5568; font-size: 14px;">
                                    <strong>Diện tích:</strong> ${m.area} m²
                                </p>
                                <p style="margin: 0 0 8px 0; color: #e53e3e; font-size: 16px; font-weight: bold;">
                ${m.price} VNĐ
                                </p>
                                <a href="${m.url}" style="display: inline-block; background: #667eea; color: white; padding: 8px 16px; text-decoration: none; border-radius: 6px; font-size: 14px;">
                                    Xem chi tiết
                                </a>
                            </div>
                        `;

                                    var infowindow = new google.maps.InfoWindow({
                                        content: infoContent
                                    });

                                    marker.addListener('click', function () {
                                        infowindow.open(map, marker);
                                    });
                                    markerObjs.push(marker);
                                });

                                if (markerObjs.length > 0) {
                                    const clusterer = new markerClusterer.MarkerClusterer({
                                        map,
                                        markers: markerObjs,
                                        renderer: {
                                            render: function (cluster, stats) {
                                                const color = cluster.count > 10
                                                        ? "#dc2626"
                                                        : cluster.count > 5
                                                        ? "#ea580c"
                                                        : "#667eea";

                                                const svg = `
                                <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40">
                                    <circle cx="20" cy="20" r="16" fill="${color}" stroke="white" stroke-width="3"/>
                                    <text x="20" y="25" text-anchor="middle" fill="white" 
                                          font-family="Arial, sans-serif" font-size="12" font-weight="bold">
                ${cluster.count}
                                    </text>
                                </svg>
                            `;

                                                return new google.maps.Marker({
                                                    position: cluster.center,
                                                    icon: {
                                                        url: "data:image/svg+xml;base64," + btoa(svg),
                                                        scaledSize: new google.maps.Size(40, 40),
                                                        anchor: new google.maps.Point(20, 20)
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }

                                // Draw circle if radius search
                                var r = parseFloat(document.getElementById('radiusKm').value || "NaN");
                                if (!isNaN(searchLat) && !isNaN(searchLng) && !isNaN(r)) {
                                    if (circle)
                                        circle.setMap(null);
                                    circle = new google.maps.Circle({
                                        strokeColor: '#667eea',
                                        strokeOpacity: 0.8,
                                        strokeWeight: 2,
                                        fillColor: '#667eea',
                                        fillOpacity: 0.15,
                                        map: map,
                                        center: {lat: searchLat, lng: searchLng},
                                        radius: r * 1000 // km -> meters
                                    });
                                    map.fitBounds(circle.getBounds());
                                }

                                // Initialize autocomplete
                                if (typeof initAutocomplete === "function")
                                    initAutocomplete();
                            }

                            // Places Autocomplete for address input
                            function initAutocomplete() {
                                var input = document.getElementById('addressInput');
                                if (!input)
                                    return;

                                var autocomplete = new google.maps.places.Autocomplete(input, {
                                    componentRestrictions: {country: 'vn'},
                                    fields: ['geometry', 'formatted_address']
                                });

                                autocomplete.addListener('place_changed', function () {
                                    var place = autocomplete.getPlace();
                                    if (!place.geometry)
                                        return;
                                    var loc = place.geometry.location;
                                    document.getElementById('lat').value = loc.lat();
                                    document.getElementById('lng').value = loc.lng();
                                });
                            }

                            // Use browser geolocation
                            document.getElementById('locBtn').addEventListener('click', function () {
                                if (!navigator.geolocation) {
                                    alert('Trình duyệt không hỗ trợ định vị GPS');
                                    return;
                                }

                                // Show loading state
                                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang lấy vị trí...';
                                this.disabled = true;

                                navigator.geolocation.getCurrentPosition(function (pos) {
                                    var lat = pos.coords.latitude, lng = pos.coords.longitude;
                                    document.getElementById('lat').value = lat;
                                    document.getElementById('lng').value = lng;

                                    // Update map center
                                    if (map) {
                                        map.setCenter({lat: lat, lng: lng});
                                        map.setZoom(15);

                                        // Add a marker for current location
                                        new google.maps.Marker({
                                            position: {lat: lat, lng: lng},
                                            map: map,
                                            title: "Vị trí hiện tại của bạn",
                                            icon: {
                                                url: 'data:image/svg+xml;base64,' + btoa('<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="#48bb78"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="3" fill="white"/></svg>'),
                                                scaledSize: new google.maps.Size(24, 24),
                                                anchor: new google.maps.Point(12, 12)
                                            }
                                        });
                                    }

                                    // Reset button
                                    document.getElementById('locBtn').innerHTML = '<i class="fas fa-location-arrow"></i> Vị trí hiện tại';
                                    document.getElementById('locBtn').disabled = false;
                                }, function (err) {
                                    alert('Không thể lấy vị trí: ' + err.message);
                                    // Reset button
                                    document.getElementById('locBtn').innerHTML = '<i class="fas fa-location-arrow"></i> Vị trí hiện tại';
                                    document.getElementById('locBtn').disabled = false;
                                }, {
                                    timeout: 10000,
                                    enableHighAccuracy: true,
                                    maximumAge: 300000 // 5 minutes
                                });
                            });

                            // Click on post card to focus on marker
                            document.addEventListener('DOMContentLoaded', function () {
                                var cards = document.querySelectorAll('.post-card');
                                cards.forEach(function (el, idx) {
                                    el.addEventListener('click', function (e) {
                                        // Don't trigger if clicking on a link
                                        if (e.target.tagName === 'A')
                                            return;

                                        var mi = parseInt(el.getAttribute('data-marker-idx'));
                                        if (!isNaN(mi) && markerObjs[mi]) {
                                            map.setCenter(markerObjs[mi].getPosition());
                                            map.setZoom(16);
                                            google.maps.event.trigger(markerObjs[mi], 'click');

                                            // Smooth scroll to map
                                            document.getElementById('map').scrollIntoView({
                                                behavior: 'smooth',
                                                block: 'center'
                                            });
                                        }
                                    });
                                });
                            });
            </script>

            <!-- Google Maps API -->
            <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBoXKWOAY7kpoON5iBnLpDaE4g8TmYdbJU&libraries=places&callback=initMap"></script>
    </body>
</html>