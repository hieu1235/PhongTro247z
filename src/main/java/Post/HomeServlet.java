package Post;

import Dal.PostDAO;
import Dal.PostImageDAO;
import Model.Post;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "HomeServlet", urlPatterns = {""})
public class HomeServlet extends HttpServlet {
    private final PostDAO postDAO = new PostDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO();
    // Để lọc bài đã duyệt, set APPROVED_STATUS = 2 (hoặc null để lấy tất cả)
    private final Integer APPROVED_STATUS = 2; // ✅ Chỉ hiển thị bài đã duyệt

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1, pageSize = 10;
        try {
            String p = req.getParameter("page");
            if (p != null) page = Math.max(1, Integer.parseInt(p));
            String ps = req.getParameter("pageSize");
            if (ps != null) pageSize = Math.max(1, Integer.parseInt(ps));
        } catch (NumberFormatException ignored) {}

        // Lấy thông tin search/filter nếu có
        String q = req.getParameter("q");
        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");
        String minAreaStr = req.getParameter("minArea");
        String maxAreaStr = req.getParameter("maxArea");

        BigDecimal minPrice = null, maxPrice = null, minArea = null, maxArea = null;
        try { if (minPriceStr!=null && !minPriceStr.isEmpty()) minPrice = new BigDecimal(minPriceStr); } catch(Exception ignored){}
        try { if (maxPriceStr!=null && !maxPriceStr.isEmpty()) maxPrice = new BigDecimal(maxPriceStr); } catch(Exception ignored){}
        try { if (minAreaStr!=null && !minAreaStr.isEmpty()) minArea = new BigDecimal(minAreaStr); } catch(Exception ignored){}
        try { if (maxAreaStr!=null && !maxAreaStr.isEmpty()) maxArea = new BigDecimal(maxAreaStr); } catch(Exception ignored){}

        // ✅ Kiểm tra có search hay không
        boolean isSearch = (q != null && !q.trim().isEmpty())
            || minPrice != null || maxPrice != null
            || minArea != null || maxArea != null;
        
        req.setAttribute("isSearch", isSearch);

        List<Post> posts;
        int total;
        
        // ✅ Load posts (có search hoặc không có search)
        if (isSearch) {
            // Tìm kiếm với filters
            posts = postDAO.search(q, minPrice, maxPrice, minArea, maxArea, page, pageSize, APPROVED_STATUS);
            total = postDAO.countSearch(q, minPrice, maxPrice, minArea, maxArea, APPROVED_STATUS);
        } else {
            // Load tất cả posts đã duyệt (trang chủ mặc định)
            posts = postDAO.search(null, null, null, null, null, page, pageSize, APPROVED_STATUS);
            total = postDAO.countSearch(null, null, null, null, null, APPROVED_STATUS);
        }
        
        // Gán thumbnail cho từng post
        for (Post p : posts) {
            String thumbnail = postImageDAO.getThumbnailByPost(p.getPostId());
            p.setThumbnail(thumbnail); // Local file URL
        }
        
        int totalPages = (int) Math.ceil((double) total / pageSize);

        req.setAttribute("posts", posts);
        req.setAttribute("page", page);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("totalPages", totalPages);
        
        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}