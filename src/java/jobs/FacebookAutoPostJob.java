package jobs;

import services.FacebookService;
import config.AppConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FacebookAutoPostJob {
    
    private ScheduledExecutorService scheduler;
    private FacebookService facebookService;
    private boolean isRunning = false;
    
    public FacebookAutoPostJob() {
        this.facebookService = new FacebookService();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * Bắt đầu job auto post
     */
    public void start() {
        if (isRunning) {
            System.out.println("Facebook Auto Post Job đã đang chạy");
            return;
        }
        
        int intervalMinutes = AppConfig.getAutoPostJobIntervalMinutes();
        
        System.out.println("Khởi động Facebook Auto Post Job với interval " + intervalMinutes + " phút");
        
        scheduler.scheduleAtFixedRate(
            this::runAutoPost, 
            0, // Initial delay
            intervalMinutes, 
            TimeUnit.MINUTES
        );
        
        isRunning = true;
    }
    
    /**
     * Dừng job
     */
    public void stop() {
        if (!isRunning) {
            return;
        }
        
        System.out.println("Dừng Facebook Auto Post Job");
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        facebookService.cleanup();
        isRunning = false;
    }
    
    /**
     * Chạy auto post job
     */
    private void runAutoPost() {
        try {
            System.out.println("Bắt đầu chạy Facebook Auto Post Job");
            
            facebookService.processPendingAutoPostListings();
            
            System.out.println("Hoàn thành Facebook Auto Post Job");
            
        } catch (Exception e) {
            System.err.println("Lỗi trong Facebook Auto Post Job: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Kiểm tra job có đang chạy không
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Chạy job ngay lập tức (manual trigger)
     */
    public void runNow() {
        if (!isRunning) {
            start();
        }
        
        // Submit immediate task
        scheduler.submit(this::runAutoPost);
    }
}