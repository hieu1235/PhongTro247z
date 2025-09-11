<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng ký - Phòng Trọ 247</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap"/>
        <style>
            /* Nội dung từ registerStyles.css */
            /* Import Google Fonts */
            @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap');

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                font-family: 'Poppins', sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                min-height: 100vh;
                padding: 20px;
            }

            form {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                border-radius: 20px;
                padding: 40px;
                width: 100%;
                max-width: 450px;
                box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
                border: 1px solid rgba(255, 255, 255, 0.2);
                animation: fadeInUp 0.6s ease;
            }

            .form__group {
                margin-bottom: 20px;
            }

            .form__flex {
                display: flex;
                flex-direction: column;
            }

            label {
                display: block;
                margin-bottom: 8px;
                color: #555;
                font-weight: 500;
                font-size: 14px;
            }

            input[type="text"],
            input[type="password"],
            input[type="email"],
            select {
                width: 100%;
                padding: 15px;
                border: 2px solid #e1e1e1;
                border-radius: 10px;
                font-size: 16px;
                font-family: 'Poppins', sans-serif;
                transition: all 0.3s ease;
                background: rgba(255, 255, 255, 0.9);
            }

            input[type="text"]:focus,
            input[type="password"]:focus,
            input[type="email"]:focus,
            select:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                background: white;
            }

            .form__error {
                color: #dc3545;
                font-size: 12px;
                margin-top: 5px;
                min-height: 16px;
                display: block;
            }

            .form__success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
                border-radius: 8px;
                padding: 12px;
                margin: 10px 0;
                text-align: center;
                font-size: 14px;
            }

            .form__checkbox {
                display: flex;
                align-items: flex-start;
                gap: 10px;
                margin-bottom: 10px;
            }

            .form__checkbox input[type="checkbox"] {
                width: auto;
                margin: 0;
                margin-top: 3px;
                accent-color: #667eea;
            }

            .form__checkbox label {
                font-size: 14px;
                line-height: 1.4;
                margin-bottom: 0;
                flex: 1;
            }

            .password-hint {
                color: #666;
                font-size: 12px;
                margin-top: 5px;
                display: block;
                font-style: italic;
            }

            select {
                background-color: white;
                background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='m6 8 4 4 4-4'/%3e%3c/svg%3e");
                background-position: right 12px center;
                background-repeat: no-repeat;
                background-size: 16px;
                padding-right: 40px;
                appearance: none;
            }

            button {
                width: 100%;
                padding: 15px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                border-radius: 10px;
                font-size: 16px;
                font-weight: 600;
                font-family: 'Poppins', sans-serif;
                cursor: pointer;
                transition: all 0.3s ease;
                margin: 20px 0;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
            }

            button:hover:not(:disabled) {
                background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
            }

            button:active {
                transform: translateY(0);
            }

            button:disabled {
                opacity: 0.7;
                cursor: not-allowed;
                transform: none !important;
            }

            .social {
                margin: 20px 0;
            }

            .social a {
                text-decoration: none;
            }

            .go {
                background: #db4437;
                color: white;
                padding: 12px;
                border-radius: 10px;
                text-align: center;
                font-weight: 500;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
            }

            .go:hover {
                background: #c23321;
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(219, 68, 55, 0.3);
            }

            h4 {
                text-align: center;
                color: #666;
                font-size: 14px;
                font-weight: 400;
                margin-top: 20px;
            }

            h4 a {
                color: #667eea;
                text-decoration: none;
                font-weight: 600;
                transition: color 0.3s ease;
            }

            h4 a:hover {
                color: #764ba2;
                text-decoration: underline;
            }

            /* Toast Message Styles */
            .toast-message {
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 15px 20px;
                border-radius: 10px;
                color: white;
                font-weight: 500;
                z-index: 1000;
                display: flex;
                align-items: center;
                gap: 10px;
                min-width: 300px;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
                animation: slideIn 0.3s ease;
            }

            .toast-message.success {
                background: linear-gradient(135deg, #28a745, #20c997);
            }

            .toast-message.error {
                background: linear-gradient(135deg, #dc3545, #e74c3c);
            }

            /* Input validation states */
            input.error,
            select.error {
                border-color: #dc3545;
                background-color: #ffeaea;
            }

            input.success,
            select.success {
                border-color: #28a745;
                background-color: #eafaf1;
            }

            /* Animations */
            @keyframes slideIn {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }

            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* Responsive Design */
            @media (max-width: 480px) {
                body {
                    padding: 10px;
                }
                
                form {
                    padding: 30px 20px;
                    max-width: 100%;
                }
                
                input[type="text"],
                input[type="password"],
                input[type="email"],
                select {
                    padding: 12px;
                    font-size: 14px;
                }
                
                button {
                    padding: 12px;
                    font-size: 14px;
                }
                
                .toast-message {
                    right: 10px;
                    left: 10px;
                    min-width: auto;
                }
                
                .form__checkbox {
                    align-items: flex-start;
                }
                
                .form__checkbox input[type="checkbox"] {
                    margin-top: 2px;
                }
            }

            @media (max-width: 360px) {
                form {
                    padding: 25px 15px;
                }
                
                .form__group {
                    margin-bottom: 15px;
                }
                
                input[type="text"],
                input[type="password"],
                input[type="email"],
                select {
                    padding: 10px;
                    font-size: 13px;
                }
                
                button {
                    padding: 10px;
                    font-size: 13px;
                }
                
                label {
                    font-size: 13px;
                }
                
                .password-hint {
                    font-size: 11px;
                }
            }

            /* Focus indicators for accessibility */
            input:focus-visible,
            select:focus-visible,
            button:focus-visible {
                outline: 2px solid #667eea;
                outline-offset: 2px;
            }

            /* Loading spinner */
            .fa-spinner {
                animation: spin 1s linear infinite;
            }

            @keyframes spin {
                from {
                    transform: rotate(0deg);
                }
                to {
                    transform: rotate(360deg);
                }
            }

            /* Nội dung từ custom.css */
            /* Custom CSS cho PhongTro247 */
            /* Import Google Fonts */
            @import @@@url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap');

            /* Global Variables */
            :root {
                --primary-color: #667eea;
                --secondary-color: #764ba2;
                --success-color: #28a745;
                --danger-color: #dc3545;
                --warning-color: #ffc107;
                --info-color: #17a2b8;
                --light-color: #f8f9fa;
                --dark-color: #343a40;
                --white: #ffffff;
                --gray-100: #f8f9fa;
                --gray-200: #e9ecef;
                --gray-300: #dee2e6;
                --gray-400: #ced4da;
                --gray-500: #adb5bd;
                --gray-600: #6c757d;
                --gray-700: #495057;
                --gray-800: #343a40;
                --gray-900: #212529;
                
                --font-family: 'Poppins', sans-serif;
                --border-radius: 10px;
                --box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                --transition: all 0.3s ease;
            }

            /* Reset CSS */
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            /* Body */
            body {
                font-family: var(--font-family);
                line-height: 1.6;
                color: var(--dark-color);
                background-color: var(--gray-100);
            }

            /* Typography */
            h1, h2, h3, h4, h5, h6 {
                font-weight: 600;
                line-height: 1.2;
                margin-bottom: 1rem;
            }

            h1 { font-size: 2.5rem; }
            h2 { font-size: 2rem; }
            h3 { font-size: 1.75rem; }
            h4 { font-size: 1.5rem; }
            h5 { font-size: 1.25rem; }
            h6 { font-size: 1rem; }

            p {
                margin-bottom: 1rem;
            }

            a {
                color: var(--primary-color);
                text-decoration: none;
                transition: var(--transition);
            }

            a:hover {
                color: var(--secondary-color);
                text-decoration: underline;
            }

            /* Container */
            .container {
                max-width: 1200px;
                margin: 0 auto;
                padding: 0 15px;
            }

            .container-fluid {
                width: 100%;
                padding: 0 15px;
            }

            /* Grid System */
            .row {
                display: flex;
                flex-wrap: wrap;
                margin: 0 -15px;
            }

            .col {
                flex: 1;
                padding: 0 15px;
            }

            .col-1 { flex: 0 0 8.333333%; max-width: 8.333333%; }
            .col-2 { flex: 0 0 16.666667%; max-width: 16.666667%; }
            .col-3 { flex: 0 0 25%; max-width: 25%; }
            .col-4 { flex: 0 0 33.333333%; max-width: 33.333333%; }
            .col-5 { flex: 0 0 41.666667%; max-width: 41.666667%; }
            .col-6 { flex: 0 0 50%; max-width: 50%; }
            .col-7 { flex: 0 0 58.333333%; max-width: 58.333333%; }
            .col-8 { flex: 0 0 66.666667%; max-width: 66.666667%; }
            .col-9 { flex: 0 0 75%; max-width: 75%; }
            .col-10 { flex: 0 0 83.333333%; max-width: 83.333333%; }
            .col-11 { flex: 0 0 91.666667%; max-width: 91.666667%; }
            .col-12 { flex: 0 0 100%; max-width: 100%; }

            /* Buttons */
            .btn {
                display: inline-block;
                padding: 12px 24px;
                font-size: 14px;
                font-weight: 500;
                text-align: center;
                text-decoration: none;
                border: none;
                border-radius: var(--border-radius);
                cursor: pointer;
                transition: var(--transition);
                font-family: var(--font-family);
                line-height: 1.5;
            }

            .btn-primary {
                background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                color: var(--white);
            }

            .btn-primary:hover {
                background: linear-gradient(135deg, var(--secondary-color), var(--primary-color));
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
                color: var(--white);
                text-decoration: none;
            }

            .btn-success {
                background-color: var(--success-color);
                color: var(--white);
            }

            .btn-success:hover {
                background-color: #218838;
                color: var(--white);
                text-decoration: none;
            }

            .btn-danger {
                background-color: var(--danger-color);
                color: var(--white);
            }

            .btn-danger:hover {
                background-color: #c82333;
                color: var(--white);
                text-decoration: none;
            }

            .btn-warning {
                background-color: var(--warning-color);
                color: var(--dark-color);
            }

            .btn-warning:hover {
                background-color: #e0a800;
                color: var(--dark-color);
                text-decoration: none;
            }

            .btn-secondary {
                background-color: var(--gray-600);
                color: var(--white);
            }

            .btn-secondary:hover {
                background-color: var(--gray-700);
                color: var(--white);
                text-decoration: none;
            }

            .btn-outline-primary {
                border: 2px solid var(--primary-color);
                color: var(--primary-color);
                background: transparent;
            }

            .btn-outline-primary:hover {
                background-color: var(--primary-color);
                color: var(--white);
                text-decoration: none;
            }

            .btn-sm {
                padding: 8px 16px;
                font-size: 12px;
            }

            .btn-lg {
                padding: 16px 32px;
                font-size: 16px;
            }

            .btn:disabled {
                opacity: 0.6;
                cursor: not-allowed;
                transform: none !important;
            }

            /* Cards */
            .card {
                background: var(--white);
                border-radius: var(--border-radius);
                box-shadow: var(--box-shadow);
                margin-bottom: 1.5rem;
                overflow: hidden;
                transition: var(--transition);
            }

            .card:hover {
                transform: translateY(-5px);
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
            }

            .card-header {
                padding: 1rem 1.5rem;
                background-color: var(--gray-100);
                border-bottom: 1px solid var(--gray-200);
                font-weight: 600;
            }

            .card-body {
                padding: 1.5rem;
            }

            .card-footer {
                padding: 1rem 1.5rem;
                background-color: var(--gray-100);
                border-top: 1px solid var(--gray-200);
            }

            /* Forms */
            .form-group {
                margin-bottom: 1.5rem;
            }

            .form-label {
                display: block;
                margin-bottom: 0.5rem;
                color: var(--gray-700);
                font-weight: 500;
                font-size: 14px;
            }

            .form-control {
                width: 100%;
                padding: 12px 15px;
                font-size: 14px;
                border: 2px solid var(--gray-300);
                border-radius: var(--border-radius);
                background-color: var(--white);
                transition: var(--transition);
                font-family: var(--font-family);
            }

            .form-control:focus {
                outline: none;
                border-color: var(--primary-color);
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            /* Alerts */
            .alert {
                padding: 15px;
                margin-bottom: 1rem;
                border-radius: var(--border-radius);
                border-left: 4px solid;
                font-weight: 500;
            }

            .alert-success {
                background-color: #d4edda;
                color: #155724;
                border-left-color: var(--success-color);
            }

            .alert-danger {
                background-color: #f8d7da;
                color: #721c24;
                border-left-color: var(--danger-color);
            }

            .alert-warning {
                background-color: #fff3cd;
                color: #856404;
                border-left-color: var(--warning-color);
            }

            .alert-info {
                background-color: #d1ecf1;
                color: #0c5460;
                border-left-color: var(--info-color);
            }

            /* Toast Messages */
            .toast-container {
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 1050;
            }

            .toast {
                background: var(--white);
                border-radius: var(--border-radius);
                box-shadow: var(--box-shadow);
                margin-bottom: 10px;
                max-width: 350px;
                animation: slideInRight 0.3s ease;
            }

            .toast.show {
                opacity: 1;
            }

            .toast-header {
                padding: 10px 15px;
                border-bottom: 1px solid var(--gray-200);
                display: flex;
                align-items: center;
                justify-content: space-between;
                font-weight: 600;
            }

            .toast-body {
                padding: 15px;
            }

            /* Navigation */
            .navbar {
                background: var(--white);
                box-shadow: var(--box-shadow);
                padding: 1rem 0;
                position: sticky;
                top: 0;
                z-index: 1000;
            }

            .navbar-brand {
                font-size: 1.5rem;
                font-weight: 700;
                color: var(--primary-color);
                text-decoration: none;
            }

            .navbar-brand:hover {
                color: var(--secondary-color);
                text-decoration: none;
            }

            .navbar-nav {
                display: flex;
                list-style: none;
                margin: 0;
                padding: 0;
                align-items: center;
            }

            .nav-item {
                margin: 0 10px;
            }

            .nav-link {
                color: var(--gray-700);
                font-weight: 500;
                padding: 8px 15px;
                border-radius: var(--border-radius);
                transition: var(--transition);
            }

            .nav-link:hover {
                color: var(--primary-color);
                background-color: var(--gray-100);
                text-decoration: none;
            }

            .nav-link.active {
                color: var(--primary-color);
                background-color: var(--gray-100);
            }

            /* Utilities */
            .text-center { text-align: center; }
            .text-left { text-align: left; }
            .text-right { text-align: right; }

            .text-primary { color: var(--primary-color); }
            .text-success { color: var(--success-color); }
            .text-danger { color: var(--danger-color); }
            .text-warning { color: var(--warning-color); }
            .text-info { color: var(--info-color); }
            .text-muted { color: var(--gray-600); }

            .bg-primary { background-color: var(--primary-color); }
            .bg-success { background-color: var(--success-color); }
            .bg-danger { background-color: var(--danger-color); }
            .bg-warning { background-color: var(--warning-color); }
            .bg-info { background-color: var(--info-color); }
            .bg-light { background-color: var(--light-color); }
            .bg-dark { background-color: var(--dark-color); }

            .d-none { display: none; }
            .d-block { display: block; }
            .d-inline { display: inline; }
            .d-inline-block { display: inline-block; }
            .d-flex { display: flex; }

            .justify-content-start { justify-content: flex-start; }
            .justify-content-end { justify-content: flex-end; }
            .justify-content-center { justify-content: center; }
            .justify-content-between { justify-content: space-between; }
            .justify-content-around { justify-content: space-around; }

            .align-items-start { align-items: flex-start; }
            .align-items-end { align-items: flex-end; }
            .align-items-center { align-items: center; }
            .align-items-stretch { align-items: stretch; }

            .mt-1 { margin-top: 0.25rem; }
            .mt-2 { margin-top: 0.5rem; }
            .mt-3 { margin-top: 1rem; }
            .mt-4 { margin-top: 1.5rem; }
            .mt-5 { margin-top: 3rem; }

            .mb-1 { margin-bottom: 0.25rem; }
            .mb-2 { margin-bottom: 0.5rem; }
            .mb-3 { margin-bottom: 1rem; }
            .mb-4 { margin-bottom: 1.5rem; }
            .mb-5 { margin-bottom: 3rem; }

            .p-1 { padding: 0.25rem; }
            .p-2 { padding: 0.5rem; }
            .p-3 { padding: 1rem; }
            .p-4 { padding: 1.5rem; }
            .p-5 { padding: 3rem; }

            /* Animations */
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }

            @keyframes fadeIn {
                from {
                    opacity: 0;
                }
                to {
                    opacity: 1;
                }
            }

            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .fade-in {
                animation: fadeIn 0.5s ease;
            }

            .fade-in-up {
                animation: fadeInUp 0.6s ease;
            }

            /* Responsive Design */
            @media (max-width: 768px) {
                .container {
                    padding: 0 10px;
                }
                
                .row {
                    margin: 0 -10px;
                }
                
                .col {
                    padding: 0 10px;
                }
                
                h1 { font-size: 2rem; }
                h2 { font-size: 1.75rem; }
                h3 { font-size: 1.5rem; }
                
                .btn {
                    padding: 10px 20px;
                    font-size: 12px;
                }
                
                .card-body {
                    padding: 1rem;
                }
                
                .toast-container {
                    right: 10px;
                    left: 10px;
                }
                
                .toast {
                    max-width: 100%;
                }
                
                .navbar-nav {
                    flex-direction: column;
                    align-items: flex-start;
                }
                
                .nav-item {
                    margin: 5px 0;
                }
            }

            @media (max-width: 576px) {
                .col-sm-12 { flex: 0 0 100%; max-width: 100%; }
                
                h1 { font-size: 1.75rem; }
                h2 { font-size: 1.5rem; }
                h3 { font-size: 1.25rem; }
                
                .btn {
                    width: 100%;
                    margin-bottom: 10px;
                }
                
                .card {
                    margin-bottom: 1rem;
                }
            }

            /* Print Styles */
            @media print {
                .btn, .navbar, .toast-container {
                    display: none !important;
                }
                
                .card {
                    box-shadow: none;
                    border: 1px solid var(--gray-300);
                }
                
                body {
                    background: white;
                    color: black;
                }
            }

            /* Nội dung từ style inline trong JSP */
            /* Additional styles for the form */
            .form__checkbox {
                display: flex;
                align-items: flex-start;
                gap: 10px;
                margin-bottom: 10px;
            }

            .form__checkbox input[type="checkbox"] {
                margin-top: 3px;
            }

            .form__checkbox label {
                font-size: 14px;
                line-height: 1.4;
            }

            .password-hint {
                color: #666;
                font-size: 12px;
                margin-top: 5px;
                display: block;
            }

            select {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 5px;
                font-family: 'Poppins', sans-serif;
                font-size: 14px;
                background-color: white;
            }

            select:focus {
                outline: none;
                border-color: #7a7aff;
                box-shadow: 0 0 5px rgba(122, 122, 255, 0.3);
            }

            .form__success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
                border-radius: 5px;
                padding: 12px;
                margin: 10px 0;
                text-align: center;
            }

            button:disabled {
                opacity: 0.6;
                cursor: not-allowed;
            }
        </style>
    </head>
    <body>
        <!-- Toast Message -->
        <c:if test="${not empty sessionScope.message}">
            <div id="toastMessage" class="toast-message ${sessionScope.messageType}">
                <c:choose>
                    <c:when test="${sessionScope.messageType == 'success'}">
                        <i class="fa fa-check-circle"></i>
                    </c:when>
                    <c:when test="${sessionScope.messageType == 'error'}">
                        <i class="fa fa-times-circle"></i>
                    </c:when>
                </c:choose>
                ${sessionScope.message}
            </div>

            <!-- Xóa message sau khi hiển thị -->
            <c:remove var="message" scope="session" />
            <c:remove var="messageType" scope="session" />
        </c:if>

        <!-- Registration Form -->
        <form action="register" method="post" id="form-register" style="height: auto; min-height: 650px;">
            <div style="text-align: center; font-size: 28px; font-weight: 600; margin-bottom: 20px;">
                Đăng ký tài khoản
            </div>

            <!-- Full Name -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="full_name">Họ và tên *</label>
                    <input type="text" placeholder="Nhập họ và tên" id="full_name" name="full_name" 
                           value="${param.full_name}" required>
                </div>
                <p class="form__error full_name__error"></p>
            </div>

            <!-- Username -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="username">Tên đăng nhập *</label>
                    <input type="text" placeholder="Nhập tên đăng nhập" id="username" name="username" 
                           value="${param.username}" required>
                </div>
                <p class="form__error username__error"></p>
            </div>

            <!-- Email -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="email">Email *</label>
                    <input type="email" placeholder="Nhập địa chỉ email" id="email" name="email" 
                           value="${param.email}" required> 
                </div>
                <p class="form__error email__error"></p>
            </div>

            <!-- Phone -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="phone">Số điện thoại *</label>
                    <input type="text" placeholder="Nhập số điện thoại" id="phone" name="phone" 
                           value="${param.phone}" required>
                </div>
                <p class="form__error phone__error"></p>
            </div>

            <!-- Password -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="password">Mật khẩu *</label>
                    <input type="password" placeholder="Nhập mật khẩu" id="password" name="password" required> 
                </div>
                <p class="form__error password__error"></p>
                <small class="password-hint">Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ và số</small>
            </div>

            <!-- Confirm Password -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="confirmPassword">Xác nhận mật khẩu *</label>
                    <input type="password" placeholder="Nhập lại mật khẩu" id="confirmPassword" name="confirmPassword" required> 
                </div>
                <p class="form__error confirmPassword__error"></p>
            </div>

            <!-- Terms and Conditions -->
            <div class="form__group">
                <div class="form__checkbox">
                    <input type="checkbox" id="terms" name="terms" required>
                    <label for="terms">
                        Tôi đồng ý với <a href="#" style="color: #7a7aff;">Điều khoản sử dụng</a> 
                        và <a href="#" style="color: #7a7aff;">Chính sách bảo mật</a>
                    </label>
                </div>
                <p class="form__error terms__error"></p>
            </div>

            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="form__error" style="margin: 10px 0; text-align: center; background: #fee; padding: 10px; border-radius: 5px; color: #d00;">
                    <i class="fa fa-exclamation-triangle"></i> ${error}
                </div>
            </c:if>

            <!-- Success Message -->
            <c:if test="${not empty message}">
                <div class="form__success" style="margin: 10px 0; text-align: center; background: #efe; padding: 10px; border-radius: 5px; color: #060;">
                    <i class="fa fa-check-circle"></i> ${message}
                </div>
            </c:if>

            <!-- Submit Button -->
            <button type="submit" id="registerBtn">
                <i class="fa fa-user-plus"></i> Đăng ký
            </button>

            <!-- Social Login -->
            <div class="social">
                <a href="https://accounts.google.com/o/oauth2/auth?scope=email%20profile%20openid&redirect_uri=http://localhost:8080/PhongTro247/register&response_type=code&client_id=370841450880-23fiie6auhj74f5f5lel16b2gujnt2ui.apps.googleusercontent.com&approval_prompt=force">
                    <div class="go" style="width: 100%">
                        <i class="fab fa-google"></i> Đăng ký với Google
                    </div>
                </a>
            </div>

            <!-- Login Link -->
            <h4 style="text-align: center; margin-top: 20px;">
                Đã có tài khoản? 
                <a href="${pageContext.request.contextPath}/login.jsp" style="color: #7a7aff;">Đăng nhập ngay</a>
            </h4>
        </form>

        <!-- Scripts -->
        <script>
            // Nội dung từ toastMessage.js
            // Toast Message Utility for PhongTro247
            class ToastMessage {
                constructor() {
                    this.container = this.createContainer();
                    this.init();
                }

                createContainer() {
                    let container = document.querySelector('.toast-container');
                    if (!container) {
                        container = document.createElement('div');
                        container.className = 'toast-container';
                        document.body.appendChild(container);
                    }
                    return container;
                }

                init() {
                    // Auto hide existing toast messages after 5 seconds
                    const existingToasts = document.querySelectorAll('.toast-message');
                    existingToasts.forEach(toast => {
                        setTimeout(() => {
                            this.hideToast(toast);
                        }, 5000);
                    });
                }

                show(message, type = 'info', duration = 5000) {
                    const toast = this.createToast(message, type);
                    this.container.appendChild(toast);

                    // Trigger animation
                    setTimeout(() => {
                        toast.classList.add('show');
                    }, 10);

                    // Auto hide
                    setTimeout(() => {
                        this.hideToast(toast);
                    }, duration);

                    return toast;
                }

                createToast(message, type) {
                    const toast = document.createElement('div');
                    toast.className = `toast-message ${type}`;
                    
                    const icon = this.getIcon(type);
                    toast.innerHTML = `
                        <i class="${icon}"></i>
                        <span>${message}</span>
                        <button type="button" class="toast-close" onclick="toastMessage.hideToast(this.parentElement)">
                            <i class="fa fa-times"></i>
                        </button>
                    `;

                    return toast;
                }

                getIcon(type) {
                    const icons = {
                        success: 'fa fa-check-circle',
                        error: 'fa fa-times-circle',
                        warning: 'fa fa-exclamation-triangle',
                        info: 'fa fa-info-circle'
                    };
                    return icons[type] || icons.info;
                }

                hideToast(toast) {
                    if (toast) {
                        toast.style.transform = 'translateX(100%)';
                        toast.style.opacity = '0';
                        setTimeout(() => {
                            if (toast.parentElement) {
                                toast.parentElement.removeChild(toast);
                            }
                        }, 300);
                    }
                }

                success(message, duration = 5000) {
                    return this.show(message, 'success', duration);
                }

                error(message, duration = 5000) {
                    return this.show(message, 'error', duration);
                }

                warning(message, duration = 5000) {
                    return this.show(message, 'warning', duration);
                }

                info(message, duration = 5000) {
                    return this.show(message, 'info', duration);
                }
            }

            // Initialize toast message utility
            const toastMessage = new ToastMessage();

            // Additional CSS for toast close button
            const style = document.createElement('style');
            style.textContent = `
                .toast-close {
                    background: none;
                    border: none;
                    color: rgba(255, 255, 255, 0.8);
                    font-size: 14px;
                    cursor: pointer;
                    padding: 0;
                    margin-left: 10px;
                    transition: color 0.3s ease;
                }
                
                .toast-close:hover {
                    color: white;
                }
            `;
            document.head.appendChild(style);
        </script>    
        <script src="./js/validationForm.js"></script>
        <script>
            // Custom validation để phù hợp với database mới
            Validator({
                form: '#form-register',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    // Full Name validation
                    Validator.isRequired('#full_name', 'Vui lòng nhập họ và tên'),
                    Validator.lengthRange('#full_name', 2, 100, 'Họ và tên phải từ 2 đến 100 ký tự'),

                    // Username validation
                    Validator.isRequired('#username', 'Vui lòng nhập tên đăng nhập'),
                    Validator.lengthRange('#username', 3, 50, 'Tên đăng nhập phải từ 3 đến 50 ký tự'),

                    // Email validation
                    Validator.isRequired('#email', 'Vui lòng nhập địa chỉ email'),
                    Validator.isEmail('#email'),

                    // Phone validation
                    Validator.isRequired('#phone', 'Vui lòng nhập số điện thoại'),
                    Validator.isPhoneNumber('#phone', 'Số điện thoại không hợp lệ (10-11 chữ số)'),

                    // Password validation
                    Validator.isRequired('#password', 'Vui lòng nhập mật khẩu'),
                    Validator.minLength('#password', 6, 'Mật khẩu phải có ít nhất 6 ký tự'),
                    Validator.isPasswordComplex('#password', 'Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số'),

                    // Confirm Password validation
                    Validator.isRequired('#confirmPassword', 'Vui lòng xác nhận mật khẩu'),
                    Validator.isConfirmed('#confirmPassword', function () {
                        return document.querySelector('#form-register #password').value;
                    }, 'Mật khẩu xác nhận không khớp'),

                    // Terms validation
                    Validator.isRequired('#terms', 'Vui lòng đồng ý với điều khoản sử dụng'),
                ],
                onsubmit: function (formValue) {
                    // Disable submit button để tránh double submit
                    const submitBtn = document.querySelector('#registerBtn');
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Đang xử lý...';

                    // Submit form
                    document.querySelector('#form-register').submit();
                }
            });

            // Custom validator cho password complexity
            if (typeof Validator !== 'undefined' && Validator.isPasswordComplex === undefined) {
                Validator.isPasswordComplex = function (selector, message) {
                    return {
                        selector: selector,
                        test: function (value) {
                            const hasLetter = /[a-zA-Z]/.test(value);
                            const hasNumber = /[0-9]/.test(value);
                            return hasLetter && hasNumber ? undefined : message || 'Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số';
                        }
                    };
                };
            }
        </script>
    </body>
</html>