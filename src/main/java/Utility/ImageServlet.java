package Utility;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet(name = "ImageServlet", urlPatterns = {"/uploads/images/*"})
public class ImageServlet extends HttpServlet {
    
    private static final String UPLOAD_DIR = "web/uploads/images/";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Remove leading slash
        String fileName = pathInfo.substring(1);
        
        // Security check - prevent directory traversal
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        Path imagePath = Paths.get(UPLOAD_DIR + fileName);
        
        if (!Files.exists(imagePath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        try {
            // Set content type based on file extension
            String contentType = getContentTypeFromFileName(fileName);
            resp.setContentType(contentType);
            
            // Set content length
            resp.setContentLengthLong(Files.size(imagePath));
            
            // Set cache headers for better performance
            resp.setHeader("Cache-Control", "public, max-age=31536000"); // 1 year
            resp.setDateHeader("Expires", System.currentTimeMillis() + 31536000000L);
            
            // Stream the file
            try (InputStream fileStream = Files.newInputStream(imagePath);
                 OutputStream outputStream = resp.getOutputStream()) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            
        } catch (IOException e) {
            System.err.println("Error serving image " + fileName + ": " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private String getContentTypeFromFileName(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lowerName.endsWith(".webp")) {
            return "image/webp";
        } else {
            return "application/octet-stream";
        }
    }
}