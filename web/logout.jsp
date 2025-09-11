<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%
    // Hủy session đăng nhập
    session.invalidate(); 
    
    // Chuyển về trang chủ
    response.sendRedirect(request.getContextPath() + "/homepage.jsp");
%>