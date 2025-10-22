package jobs;

import Service.PostSchedulerService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.logging.Logger;

/**
 * Quartz Job để tự động xử lý scheduled posts
 * Chạy định kỳ để check và publish các posts đã lập lịch
 */
public class ScheduledPostJob implements Job {

    private static final Logger logger = Logger.getLogger(ScheduledPostJob.class.getName());
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("ScheduledPostJob started execution");
        
        try {
            PostSchedulerService schedulerService = new PostSchedulerService();
            int publishedCount = schedulerService.processScheduledPosts();
            
            if (publishedCount > 0) {
                logger.info("ScheduledPostJob completed: " + publishedCount + " posts published");
            } else {
                logger.fine("ScheduledPostJob completed: No posts to publish");
            }
            
        } catch (Exception e) {
            logger.severe("Error in ScheduledPostJob execution: " + e.getMessage());
            e.printStackTrace();
            throw new JobExecutionException("Failed to process scheduled posts", e);
        }
    }
}