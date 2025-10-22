package Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LocalFileService {
    
    private static final String UPLOAD_DIR = "web/uploads/images/";
    private static final String WEB_PATH = "/uploads/images/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
    
    public LocalFileService() {
        // Tạo thư mục upload nếu chưa có
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            System.err.println("Không thể tạo thư mục upload: " + e.getMessage());
        }
    }
    
    public String uploadImage(byte[] imageBytes, int userId) throws Exception {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("File không hợp lệ hoặc rỗng");
        }
        
        if (imageBytes.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File quá lớn. Tối đa " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
        
        // Tạo tên file unique
        String fileName = "user_" + userId + "_" + System.currentTimeMillis() + ".jpg";
        String filePath = UPLOAD_DIR + fileName;
        
        try {
            // Lưu file vào local storage
            Path targetPath = Paths.get(filePath);
            Files.write(targetPath, imageBytes);
            
            // Trả về web path để hiển thị
            return WEB_PATH + fileName;
            
        } catch (IOException e) {
            throw new Exception("Lỗi lưu file: " + e.getMessage());
        }
    }
    
    public boolean deleteImageByUrl(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return false;
            }
            
            // Convert web path back to file path
            if (imageUrl.startsWith(WEB_PATH)) {
                String fileName = imageUrl.substring(WEB_PATH.length());
                String filePath = UPLOAD_DIR + fileName;
                Path targetPath = Paths.get(filePath);
                
                if (Files.exists(targetPath)) {
                    Files.delete(targetPath);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi xóa file: " + e.getMessage());
        }
        return false;
    }
    
    public int deleteMultipleImages(List<String> imageUrls) {
        int deletedCount = 0;
        
        if (imageUrls == null || imageUrls.isEmpty()) {
            return deletedCount;
        }
        
        for (String imageUrl : imageUrls) {
            if (deleteImageByUrl(imageUrl)) {
                deletedCount++;
            }
        }
        
        return deletedCount;
    }
    
    public static boolean isValidFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= MAX_FILE_SIZE;
    }
    
    public static boolean isValidImageExtension(String fileName) {
        if (fileName == null) return false;
        
        String lowerName = fileName.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lowerName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}