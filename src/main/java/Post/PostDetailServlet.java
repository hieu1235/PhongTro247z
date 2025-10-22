package Post;

import Dal.PostDAO;
import Dal.PostImageDAO;
import Model.Post;
import Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PostDetailServlet", urlPatterns = {"/post/detail"})
public class PostDetailServlet extends HttpServlet {
    private final PostDAO postDAO = new PostDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            Post post = postDAO.findById(id).orElse(null);
            if (post == null) {
                req.setAttribute("error", "Bài đăng không tồn tại.");
                req.getRequestDispatcher("/post_detail.jsp").forward(req, resp);
                return;
            }
            List<String> images = postImageDAO.findByPost(id);
            req.setAttribute("post", post);
            req.setAttribute("images", images);

            // Kiểm tra quyền sửa/xóa (nếu muốn hiển thị)
            HttpSession session = req.getSession(false);
            boolean isOwner = false;
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                if (user.getUserId() == post.getUserId()) isOwner = true;
            }
            req.setAttribute("isOwner", isOwner);

            req.getRequestDispatcher("/post_detail.jsp").forward(req, resp);
        } catch (Exception ex) {
            System.out.println("PostDetailServlet error: " + ex.getMessage());
            ex.printStackTrace();
            req.setAttribute("error", "Lỗi khi xem bài.");
            req.getRequestDispatcher("/post_detail.jsp").forward(req, resp);
        }
    }
}