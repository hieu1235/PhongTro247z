package Post; // Hoặc package bạn muốn đặt

import Dal.PostDAO;
import Dal.PostImageDAO;
import Model.Post;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet này được cấu hình để làm trang chủ, xử lý đường dẫn gốc "/".
 */
@WebServlet("") // <-- Dòng quan trọng nhất, biến servlet này thành trang chủ
public class TrangChuServlet extends HttpServlet {

    private final PostDAO postDAO = new PostDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO();
    private static final int APPROVED_STATUS = 2; // Chỉ hiển thị bài đã duyệt

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Lấy tất cả các bài viết đã được duyệt để hiển thị trên trang chủ
        List<Post> posts = postDAO.search(null, null, null, null, null, 1, 10, APPROVED_STATUS);

        // Gán ảnh thumbnail cho mỗi bài viết
        for (Post p : posts) {
            String thumbnail = postImageDAO.getThumbnailByPost(p.getPostId());
            p.setThumbnail(thumbnail);
        }

        req.setAttribute("posts", posts);
        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}