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

@WebServlet(name = "MyPostsServlet", urlPatterns = {"/post/my"})
public class MyPostsServlet extends HttpServlet {

    private final PostDAO postDAO = new PostDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO(); // ✅ Thêm

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        int page = 1;
        int pageSize = 10;
        try {
            String p = req.getParameter("page");
            if (p != null) page = Math.max(1, Integer.parseInt(p));
            String ps = req.getParameter("pageSize");
            if (ps != null) pageSize = Math.max(1, Integer.parseInt(ps));
        } catch (NumberFormatException ignored) {}

        try {
            List<Post> list = postDAO.findByUser(user.getUserId(), page, pageSize);
            
            // ✅ Gán thumbnail cho từng post của user
            for (Post p : list) {
                String thumbnail = postImageDAO.getThumbnailByPost(p.getPostId());
                p.setThumbnail(thumbnail);
            }
            
            int total = postDAO.countByUser(user.getUserId());
            int totalPages = (int) Math.ceil((double) total / pageSize);

            req.setAttribute("posts", list);
            req.setAttribute("page", page);
            req.setAttribute("pageSize", pageSize);
            req.setAttribute("totalPages", totalPages);
            req.getRequestDispatcher("/my_posts.jsp").forward(req, resp);
        } catch (Exception ex) {
            System.out.println("MyPostsServlet error: " + ex.getMessage());
            ex.printStackTrace();
            req.setAttribute("error", "Không thể lấy danh sách bài: " + ex.getMessage());
            req.getRequestDispatcher("/my_posts.jsp").forward(req, resp);
        }
    }
}