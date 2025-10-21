/*
 * Utility class for uploading single image to local server
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

/**
 * Utility class for uploading single image
 * @author PhongTro247z
 */
public class UploadImage {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"};

    /**
     * Upload a single image from HTTP request with validation
     * @param request HTTP request containing the file
     * @param inputName Name of the input field in the form
     * @param uploadFolderPath Absolute path to upload folder (e.g., "web/uploads/images/")
     * @param userId User ID for unique file naming
     * @return Web path if upload successful (e.g., "/uploads/images/user_1_123456.jpg"), null otherwise
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    public String uploadImage(HttpServletRequest request, String inputName, String uploadFolderPath, int userId)
            throws ServletException, IOException {
        
        // Create upload directory if not exists
        File uploadDir = new File(uploadFolderPath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new IOException("Cannot create upload directory: " + uploadFolderPath);
            }
        }

        // Get file part from form
        Part filePart = request.getPart(inputName);
        if (filePart == null || filePart.getSize() == 0) {
            return null; // No file uploaded
        }

        // Validate file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File quá lớn (tối đa 5MB)");
        }

        // Validate file type (only accept images)
        String contentType = filePart.getContentType();
        if (!isAllowedImageType(contentType)) {
            throw new IOException("File không phải là ảnh hợp lệ");
        }

        // Get file name and extension
        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (originalFileName == null || originalFileName.isEmpty()) {
            return null;
        }
        
        String extension = getFileExtension(originalFileName);
        
        // Create unique file name
        String uniqueFileName = "user_" + userId + "_" + System.currentTimeMillis() + extension;
        File filePath = new File(uploadFolderPath + File.separator + uniqueFileName);

        // Save file to server
        Files.copy(filePart.getInputStream(), filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("UploadImage: Image uploaded successfully - " + uniqueFileName);
        
        // Return web path (for database and display)
        return "/uploads/images/" + uniqueFileName;
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
