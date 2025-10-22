package Post;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import Dal.PostDAO;
import Model.Post;
import Model.User;

@WebServlet(name = "UpdateScheduleServlet", urlPatterns = {"/post/updateSchedule"})
public class UpdateScheduleServlet extends HttpServlet {

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
            String scheduledTimeStr = request.getParameter("scheduledTime");
            
            if (postIdStr == null || scheduledTimeStr == null) {
                session.setAttribute("error", "Thông tin không đầy đủ!");
                response.sendRedirect(request.getContextPath() + "/post/my");
                return;
            }
            
            int postId = Integer.parseInt(postIdStr);
            
            // Verify post ownership
            Post post = postDAO.getPostById(postId);
            if (post == null || post.getUserId() != currentUser.getUserId()) {
                session.setAttribute("error", "Bạn không có quyền chỉnh sửa tin đăng này!");
                response.sendRedirect(request.getContextPath() + "/post/my");
                return;
            }
            
            // Verify post is scheduled
            if (!"SCHEDULED".equals(post.getStatusName())) {
                session.setAttribute("error", "Chỉ có thể chỉnh sửa thời gian của tin đăng đã lập lịch!");
                response.sendRedirect(request.getContextPath() + "/post/my");
                return;
            }
            
            // Parse scheduled time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date scheduledDate = dateFormat.parse(scheduledTimeStr);
            Timestamp scheduledAt = new Timestamp(scheduledDate.getTime());
            
            // Validate scheduled time is in future
            if (scheduledAt.before(new Timestamp(System.currentTimeMillis()))) {
                session.setAttribute("error", "Thời gian đăng phải sau thời điểm hiện tại!");
                response.sendRedirect(request.getContextPath() + "/post/my");
                return;
            }
            
            // Update scheduled time
            boolean success = postDAO.updateScheduledTime(postId, scheduledAt);
            
            if (success) {
                session.setAttribute("success", "Đã cập nhật thời gian đăng thành công!");
            } else {
                session.setAttribute("error", "Có lỗi xảy ra khi cập nhật thời gian đăng!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/post/my");
    }
}