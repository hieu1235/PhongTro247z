package Post;

import Dal.PostDAO;
import Dal.PostImageDAO;
import DBcontext.DBContext;
import Model.Post;
import Model.User;
import Service.LocalFileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "DeletePostServlet", urlPatterns = {"/post/delete"})
public class DeletePostServlet extends HttpServlet {

    private final PostDAO postDAO = new PostDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO();
    private final LocalFileService fileService = new LocalFileService(); // ✅ Sử dụng LocalFileService

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/post/my");
            return;
        }

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            int id = Integer.parseInt(idStr);
            Post post = postDAO.findById(id).orElse(null);
            if (post == null || post.getUserId() != user.getUserId()) {
                conn.rollback();
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // ✅ Lấy image URLs trước khi xóa
            List<String> imageUrls = postImageDAO.findByPost(id);
            
            // Xóa records trong database
            postImageDAO.deleteByPost(id, conn);
            postDAO.delete(id, conn);
            conn.commit();

            // ✅ Xóa ảnh trên local storage sau khi xóa thành công trong DB
            if (!imageUrls.isEmpty()) {
                System.out.println("DeletePostServlet: Deleting " + imageUrls.size() + " images from local storage for post " + id);
                int deletedCount = fileService.deleteMultipleImages(imageUrls);
                System.out.println("DeletePostServlet: Successfully deleted " + deletedCount + "/" + imageUrls.size() + " images from local storage");
            }

            System.out.println("DeletePostServlet: user=" + user.getUserId() + " deleted post=" + id + " with local file cleanup");
            resp.sendRedirect(req.getContextPath() + "/post/my?msg=deleted");
            
        } catch (Exception ex) {
            System.out.println("DeletePostServlet error: " + ex.getMessage());
            ex.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/post/my?err=delete");
        }
    }
}