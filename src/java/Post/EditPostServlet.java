package Post;

import Model.Post;
import Model.User;
import Service.PostService;
import Service.LocalFileService;
import Dal.PostDAO;
import Dal.PostImageDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

@WebServlet(name = "EditPostServlet", urlPatterns = {"/post/edit"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 8 * 1024 * 1024, maxRequestSize = 40 * 1024 * 1024)
public class EditPostServlet extends HttpServlet {

    private final PostService postService = new PostService();
    private final PostDAO postDAO = new PostDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        try {
            int id = Integer.parseInt(idStr);
            Post post = postDAO.findById(id).orElse(null);
            if (post == null || post.getUserId() != user.getUserId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            // ✅ Lấy image URLs từ local storage
            List<String> images = postImageDAO.findByPost(id);
            req.setAttribute("post", post);
            req.setAttribute("images", images); // Local file URLs
            req.getRequestDispatcher("/post_edit.jsp").forward(req, resp);
        } catch (Exception ex) {
            System.out.println("EditPostServlet GET error: " + ex.getMessage());
            ex.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/post/my?err=load");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        String idStr = req.getParameter("postId");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/post/my");
            return;
        }

        int postId = Integer.parseInt(idStr);
        Post existing = postDAO.findById(postId).orElse(null);
        if (existing == null || existing.getUserId() != user.getUserId()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String address = req.getParameter("address");
        String latStr = req.getParameter("lat");
        String lngStr = req.getParameter("lng");
        String priceStr = req.getParameter("price");
        String areaStr = req.getParameter("area");
        
        // ✅ Handle scheduled posting parameters
        String publishType = req.getParameter("publishType");
        String scheduledAtStr = req.getParameter("scheduledAt");
        boolean isScheduled = "scheduled".equals(publishType);
        LocalDateTime scheduledAt = null;
        
        if (isScheduled) {
            if (scheduledAtStr == null || scheduledAtStr.trim().isEmpty()) {
                req.setAttribute("error", "Vui lòng chọn thời gian đăng tin");
                req.setAttribute("post", existing);
                req.setAttribute("images", postImageDAO.findByPost(existing.getPostId()));
                req.getRequestDispatcher("/post_edit.jsp").forward(req, resp);
                return;
            }
            
            try {
                scheduledAt = LocalDateTime.parse(scheduledAtStr);
                if (scheduledAt.isBefore(LocalDateTime.now().plusMinutes(5))) {
                    req.setAttribute("error", "Thời gian đăng tin phải sau ít nhất 5 phút");
                    req.setAttribute("post", existing);
                    req.setAttribute("images", postImageDAO.findByPost(existing.getPostId()));
                    req.getRequestDispatcher("/post_edit.jsp").forward(req, resp);
                    return;
                }
            } catch (DateTimeParseException e) {
                req.setAttribute("error", "Thời gian đăng tin không hợp lệ");
                req.setAttribute("post", existing);
                req.setAttribute("images", postImageDAO.findByPost(existing.getPostId()));
                req.getRequestDispatcher("/post_edit.jsp").forward(req, resp);
                return;
            }
        }

        if (title == null || title.trim().isEmpty()) {
            req.setAttribute("error", "Tiêu đề không được để trống");
            req.setAttribute("post", existing);
            req.setAttribute("images", postImageDAO.findByPost(existing.getPostId()));
            req.getRequestDispatcher("/post_edit.jsp").forward(req, resp);
            return;
        }

        existing.setTitle(title.trim());
        existing.setContent(content != null ? content.trim() : "");
        existing.setAddress(address != null ? address.trim() : "");
        try {
            if (latStr != null && !latStr.isEmpty()) existing.setLat(new BigDecimal(latStr));
            else existing.setLat(null);
            if (lngStr != null && !lngStr.isEmpty()) existing.setLng(new BigDecimal(lngStr));
            else existing.setLng(null);
            if (priceStr != null && !priceStr.isEmpty()) existing.setPrice(new BigDecimal(priceStr));
            else existing.setPrice(null);
            if (areaStr != null && !areaStr.isEmpty()) existing.setArea(new BigDecimal(areaStr));
            else existing.setArea(null);
        } catch (NumberFormatException nfe) {
            req.setAttribute("error", "Giá trị lat/lng/price/area không hợp lệ");
            req.setAttribute("post", existing);
            req.setAttribute("images", postImageDAO.findByPost(existing.getPostId()));
            req.getRequestDispatcher("/post_edit.jsp").forward(req, resp);
            return;
        }
        
        // ✅ Update scheduled posting fields
        if (isScheduled) {
            existing.setScheduledAt(Timestamp.valueOf(scheduledAt));
            existing.setAutoPublish(true);
            // If changing to scheduled, set status to SCHEDULED
            if (existing.getStatusId() != 5) {
                existing.setStatusId(5); // SCHEDULED
            }
        } else {
            // If changing from scheduled to immediate, clear scheduling
            if (existing.getScheduledAt() != null) {
                existing.setScheduledAt(null);
                existing.setAutoPublish(false);
                // If was scheduled, set to PENDING for re-evaluation
                if (existing.getStatusId() == 5) {
                    existing.setStatusId(1); // PENDING
                }
            }
        }

        // ✅ Upload ảnh mới lên Local File Storage
        Collection<Part> parts = req.getParts();
        
        // Convert Parts to byte arrays (similar to CreatePostServlet) với validation
        List<byte[]> newImageBytes = new ArrayList<>();
        if (parts != null) {
            for (Part part : parts) {
                if (part != null && "images".equals(part.getName()) && part.getSize() > 0) {
                    // Validate file size
                    if (!LocalFileService.isValidFileSize(part.getSize())) {
                        req.setAttribute("error", "File quá lớn. Tối đa 5MB");
                        req.setAttribute("post", existing);
                        req.setAttribute("images", postImageDAO.findByPost(existing.getPostId()));
                        req.getRequestDispatcher("/post_edit.jsp").forward(req, resp);
                        return;
                    }
                    
                    String contentType = part.getContentType();
                    if (contentType != null && contentType.startsWith("image/")) {
                        try (java.io.InputStream inputStream = part.getInputStream()) {
                            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                            int bytesRead;
                            byte[] data = new byte[8192];
                            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, bytesRead);
                            }
                            newImageBytes.add(buffer.toByteArray());
                        }
                    }
                }
            }
        }

        try {
            // Update post với Local File upload
            postService.updatePostWithImages(existing, newImageBytes);
            System.out.println("EditPostServlet: user=" + user.getUserId() + " updated post=" + existing.getPostId() + " with local storage");
            resp.sendRedirect(req.getContextPath() + "/post/my?msg=updated");
        } catch (Exception ex) {
            System.out.println("EditPostServlet POST error: " + ex.getMessage());
            ex.printStackTrace();
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("post", existing);
            req.setAttribute("images", postImageDAO.findByPost(existing.getPostId()));
            req.getRequestDispatcher("/post_edit.jsp").forward(req, resp);
        }
    }
}