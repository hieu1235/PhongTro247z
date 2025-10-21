package Post;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import Dal.PostDAO;
import Model.Post;
import Model.User;

@WebServlet(name = "PublishNowServlet", urlPatterns = {"/post/publishNow"})
public class PublishNowServlet extends HttpServlet {

    private PostDAO postDAO;

    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Get parameters
            String postIdStr = request.getParameter("id");
            
            if (postIdStr == null) {
                session.setAttribute("error", "Thông tin không đầy đủ!");
                response.sendRedirect(request.getContextPath() + "/post/my");
                return;
            }
            
            int postId = Integer.parseInt(postIdStr);
            
            // Verify post ownership
            Post post = postDAO.getPostById(postId);
            if (post == null || post.getUserId() != currentUser.getUserId()) {
                session.setAttribute("error", "Bạn không có quyền đăng tin này!");
                response.sendRedirect(request.getContextPath() + "/post/my");
                return;
            }
            
            // Verify post is scheduled
            if (!"SCHEDULED".equals(post.getStatusName())) {
                session.setAttribute("error", "Chỉ có thể đăng ngay những tin đã lập lịch!");
                response.sendRedirect(request.getContextPath() + "/post/my");
                return;
            }
            
            // Publish the post now
            boolean success = postDAO.publishScheduledPost(postId);
            
            if (success) {
                session.setAttribute("success", "Đã đăng tin thành công!");
            } else {
                session.setAttribute("error", "Có lỗi xảy ra khi đăng tin!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/post/my");
    }
}