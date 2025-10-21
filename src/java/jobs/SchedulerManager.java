package jobs;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * ServletContextListener để khởi tạo và quản lý Quartz Scheduler
 * Tự động start/stop scheduler khi webapp start/stop
 */
@WebListener
public class SchedulerManager implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(SchedulerManager.class.getName());
    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing Quartz Scheduler for Scheduled Posts");
        
        try {
            // Tạo scheduler factory
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();

            // Tạo job detail
            JobDetail jobDetail = JobBuilder.newJob(ScheduledPostJob.class)
                    .withIdentity("scheduledPostJob", "postGroup")
                    .withDescription("Job to process scheduled posts")
                    .build();

            // Tạo trigger - chạy mỗi 5 phút
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("scheduledPostTrigger", "postGroup")
                    .withDescription("Trigger for scheduled post processing")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMinutes(5) // Chạy mỗi 5 phút
                            .repeatForever())
                    .build();

            // Schedule job
            scheduler.scheduleJob(jobDetail, trigger);
            
            // Start scheduler
            scheduler.start();
            
            logger.info("Quartz Scheduler started successfully. ScheduledPostJob will run every 5 minutes.");
            
        } catch (SchedulerException e) {
            logger.severe("Failed to start Quartz Scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down Quartz Scheduler");
        
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown(true); // true = wait for jobs to complete
                logger.info("Quartz Scheduler shut down successfully");
            }
        } catch (SchedulerException e) {
            logger.severe("Error shutting down Quartz Scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Trigger manual execution của scheduled post job
     * Có thể được gọi từ admin interface
     */
    public static void triggerManualExecution() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            
            if (scheduler.isStarted()) {
                JobKey jobKey = new JobKey("scheduledPostJob", "postGroup");
                scheduler.triggerJob(jobKey);
                logger.info("Manual trigger of ScheduledPostJob executed");
            }
        } catch (SchedulerException e) {
            logger.severe("Error triggering manual execution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}