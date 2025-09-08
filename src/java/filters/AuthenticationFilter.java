package filters;

import utils.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/secure/*")
public class AuthenticationFilter implements Filter {
    
    // Các URL không cần authenticate
    private List<String> excludeUrls = Arrays.asList(
        "/login", "/register", "/logout", "/public", "/assets", "/css", "/js", "/images"
    );
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo filter
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Kiểm tra xem URL có nằm trong danh sách exclude không
        boolean excluded = excludeUrls.stream().anyMatch(path::startsWith);
        
        if (excluded || SessionUtil.isUserLoggedIn(httpRequest)) {
            // Cho phép truy cập
            chain.doFilter(request, response);
        } else {
            // Redirect đến trang login
            httpResponse.sendRedirect(contextPath + "/login?redirect=" + 
                                    java.net.URLEncoder.encode(requestURI, "UTF-8"));
        }
    }
    
    @Override
    public void destroy() {
        // Cleanup
    }
}