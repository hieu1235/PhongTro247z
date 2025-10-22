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

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    private final PostDAO postDAO = new PostDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO();
    private final Integer APPROVED_STATUS = 2; // Chỉ hiển thị bài đã duyệt

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1, pageSize = 10;
        try {
            String p = req.getParameter("page");
            if (p != null) page = Math.max(1, Integer.parseInt(p));
            String ps = req.getParameter("pageSize");
            if (ps != null) pageSize = Math.max(1, Integer.parseInt(ps));
        } catch (NumberFormatException ignored) {}

        // Lấy search parameters
        String q = req.getParameter("q");
        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");
        String minAreaStr = req.getParameter("minArea");
        String maxAreaStr = req.getParameter("maxArea");
        String latStr = req.getParameter("lat");
        String lngStr = req.getParameter("lng");
        String radiusKmStr = req.getParameter("radiusKm");

        // Parse numeric parameters
        BigDecimal minPrice = null, maxPrice = null, minArea = null, maxArea = null;
        Double lat = null, lng = null, radiusKm = null;

        try { if (minPriceStr != null && !minPriceStr.isEmpty()) minPrice = new BigDecimal(minPriceStr); } catch(Exception ignored){}
        try { if (maxPriceStr != null && !maxPriceStr.isEmpty()) maxPrice = new BigDecimal(maxPriceStr); } catch(Exception ignored){}
        try { if (minAreaStr != null && !minAreaStr.isEmpty()) minArea = new BigDecimal(minAreaStr); } catch(Exception ignored){}
        try { if (maxAreaStr != null && !maxAreaStr.isEmpty()) maxArea = new BigDecimal(maxAreaStr); } catch(Exception ignored){}
        
        try { if (latStr != null && !latStr.isEmpty()) lat = Double.parseDouble(latStr); } catch(Exception ignored){}
        try { if (lngStr != null && !lngStr.isEmpty()) lng = Double.parseDouble(lngStr); } catch(Exception ignored){}
        try { if (radiusKmStr != null && !radiusKmStr.isEmpty()) radiusKm = Double.parseDouble(radiusKmStr); } catch(Exception ignored){}

        // 🔍 DEBUG: Log search parameters
        System.out.println("=== SEARCH PARAMETERS ===");
        System.out.println("Query (q): " + q);
        System.out.println("Lat: " + lat + " (raw: " + latStr + ")");
        System.out.println("Lng: " + lng + " (raw: " + lngStr + ")");
        System.out.println("RadiusKm: " + radiusKm + " (raw: " + radiusKmStr + ")");
        System.out.println("MinPrice: " + minPrice + ", MaxPrice: " + maxPrice);
        System.out.println("MinArea: " + minArea + ", MaxArea: " + maxArea);
        System.out.println("========================");

        // Set attributes cho JSP
        req.setAttribute("lat", lat);
        req.setAttribute("lng", lng);
        req.setAttribute("radiusKm", radiusKm);

        // Kiểm tra có search hay không
        boolean hasSearch = (q != null && !q.trim().isEmpty())
                || minPrice != null || maxPrice != null
                || minArea != null || maxArea != null
                || lat != null || lng != null;

        req.setAttribute("isSearch", hasSearch); // ✅ Add this for JSP

        if (hasSearch) {
            // Sử dụng searchWithLocation để hỗ trợ tìm kiếm theo bán kính
            List<Post> posts = postDAO.searchWithLocation(
                q, minPrice, maxPrice, minArea, maxArea,
                lat, lng, radiusKm,
                page, pageSize, APPROVED_STATUS
            );

            // Gán thumbnail cho posts
            for (Post post : posts) {
                String thumbnail = postImageDAO.getThumbnailByPost(post.getPostId());
                post.setThumbnail(thumbnail);
            }

            // Đếm tổng số kết quả
            int total = postDAO.countSearchWithLocation(
                q, minPrice, maxPrice, minArea, maxArea,
                lat, lng, radiusKm, APPROVED_STATUS
            );
            int totalPages = (int) Math.ceil((double) total / pageSize);

            req.setAttribute("posts", posts);
            req.setAttribute("page", page);
            req.setAttribute("pageSize", pageSize);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("hasSearch", true);
        } else {
            req.setAttribute("hasSearch", false);
        }

        // Forward đến home.jsp
        req.getRequestDispatcher("/home.jsp").forward(req, resp);
    }
}