/*
 * Utility class for uploading multiple images to local server
 * Compatible with PhongTro247z project structure
 */
package Utility;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for uploading multiple images
 * @author PhongTro247z
 */
public class UploadMultipleImages {
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_IMAGES = 5;
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"};
    
    /**
     * Upload multiple images from HTTP request with validation
     * @param request HTTP request containing the files
     * @param inputName Name of the input field in the form (same name for multiple files)
     * @param uploadFolderPath Absolute path to upload folder (e.g., "web/uploads/images/")
     * @param userId User ID for unique file naming
     * @return List of web paths for uploaded files (e.g., "/uploads/images/user_1_123456.jpg")
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    public List<String> uploadImages(HttpServletRequest request, String inputName, String uploadFolderPath, int userId)
            throws ServletException, IOException {

        List<String> uploadedWebPaths = new ArrayList<>();

        // Create upload directory if not exists
        File uploadDir = new File(uploadFolderPath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new IOException("Cannot create upload directory: " + uploadFolderPath);
            }
        }

        // Validate and collect image parts
        List<Part> imageParts = new ArrayList<>();
        for (Part part : request.getParts()) {
            if (part.getName().equals(inputName) && part.getSize() > 0) {
                // Validate file size
                if (part.getSize() > MAX_FILE_SIZE) {
                    throw new IOException("File " + part.getSubmittedFileName() + " quá lớn (tối đa 5MB)");
                }
                
                // Validate file type
                String contentType = part.getContentType();
                if (!isAllowedImageType(contentType)) {
                    throw new IOException("File " + part.getSubmittedFileName() + " không phải là ảnh hợp lệ");
                }
                
                imageParts.add(part);
            }
        }
        
        // Check max images limit
        if (imageParts.size() > MAX_IMAGES) {
            throw new IOException("Chỉ được upload tối đa " + MAX_IMAGES + " ảnh");
        }

        // Process all valid image parts
        for (Part part : imageParts) {
            String originalFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            String extension = getFileExtension(originalFileName);
            
            // Create unique file name
            String uniqueFileName = "user_" + userId + "_" + System.currentTimeMillis() + extension;
            File filePath = new File(uploadFolderPath + File.separator + uniqueFileName);
            
            // Save file to server
            Files.copy(part.getInputStream(), filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            // Return web path (for database and display)
            String webPath = "/uploads/images/" + uniqueFileName;
            uploadedWebPaths.add(webPath);
            
            System.out.println("UploadMultipleImages: Uploaded - " + uniqueFileName);
        }

        System.out.println("UploadMultipleImages: Total uploaded - " + uploadedWebPaths.size() + " images");
        return uploadedWebPaths;
    }

    /**
     * Get number of uploaded images from request
     * @param request HTTP request containing the files
     * @param inputName Name of the input field
     * @return Number of valid image files
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    public int countUploadedImages(HttpServletRequest request, String inputName)
            throws ServletException, IOException {
        
        int count = 0;
        for (Part part : request.getParts()) {
            if (part.getName().equals(inputName)
                    && part.getSize() > 0
                    && isAllowedImageType(part.getContentType())) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Validate if content type is allowed image type
     */
    private boolean isAllowedImageType(String contentType) {
        if (contentType == null) return false;
        
        for (String allowedType : ALLOWED_TYPES) {
            if (contentType.equalsIgnoreCase(allowedType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg"; // default extension
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
