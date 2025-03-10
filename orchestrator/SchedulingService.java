package com.arabbank.hdf.uam.brain.orchestrator;

import com.arabbank.hdf.uam.brain.utils.UamUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingService {
    private static final String JOB_BASE_PACKAGE = "com/arabbank/hdf/uam/brain";
    private final Scheduler scheduler;
    private final JobDao jobDao;
    private final JobListener jobListener;

    public static final String BRAIN_JOBS_GROUP_NAME = "brain";
    public static final String TRIGGER_ID_SUFFIX = "_TRIGGER";

    @Getter
    private final List<String> activeJobKeys = new ArrayList<>();

    public void scheduleTasks() {
        try {
            // Add the job listener to the scheduler
            scheduler.getListenerManager().addJobListener(jobListener);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to add job listener", e);
        }

        // Get the valid jobs to schedule
        Map<JobInfo, Class<? extends Job>> validJobsMap = getValidJobs();
        log.info("Scheduling {} jobs", validJobsMap.size());

        for (Map.Entry<JobInfo, Class<? extends Job>> entry : validJobsMap.entrySet()) {
            JobInfo job = entry.getKey();
            Class<? extends Job> jobClass = entry.getValue();
            log.info("Scheduling job '{}' with CRON ({})", job.getCode(), job.getCron());
            activeJobKeys.add(job.getCode());

            try {
                // Create a JobKey for the current job
                JobKey jobKey = new JobKey(job.getCode(), BRAIN_JOBS_GROUP_NAME);

                // Check if the job already exists
                if (scheduler.checkExists(jobKey)) {
                    log.info("Job '{}' already exists. Rescheduling.", job.getCode());

                    // Update the existing trigger
                    TriggerKey triggerKey = new TriggerKey(job.getCode() + TRIGGER_ID_SUFFIX, BRAIN_JOBS_GROUP_NAME);
                    CronTrigger newTrigger = TriggerBuilder.newTrigger()
                            .withIdentity(triggerKey)
                            .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                            .build();

                    // Reschedule the job with the new trigger
                    scheduler.rescheduleJob(triggerKey, newTrigger);
                } else {
                    // Define the job detail and trigger if it doesn't exist
                    JobDetail jobDetail = JobBuilder.newJob(jobClass)
                            .withIdentity(jobKey)
                            .build();

                    CronTrigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity(job.getCode() + TRIGGER_ID_SUFFIX, BRAIN_JOBS_GROUP_NAME)
                            .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                            .build();

                    // Schedule the job with the trigger
                    scheduler.scheduleJob(jobDetail, trigger);
                }

                log.info("Done scheduling job '{}'", job.getCode());
            } catch (SchedulerException e) {
                log.error("Failed to schedule job: " + job.getCode(), e);
            }
        }
    }

    /**
     * @return Map with {@link JobInfo} as key, and 'job class' as value.
     */
    private Map<JobInfo, Class<? extends Job>> getValidJobs() {
        Map<String, Class<?>> jobClassMap = getJobMap();
        List<JobInfo> activeJobs = getJobInfoList();
        Map<JobInfo, Class<? extends Job>> validJobs = new HashMap<>();

        for (JobInfo job : activeJobs) {
            Class<?> jobClass = jobClassMap.get(job.getCode());

            if (jobClass == null) {
                log.error("Could not find class for job: {}", job.getCode());
                continue;
            }

            if (!Job.class.isAssignableFrom(jobClass)) {
                log.error("Class {} does not implement Job interface", jobClass.getName());
                continue;
            }

            //noinspection unchecked
            validJobs.put(job, (Class<? extends Job>) jobClass);
        }

        return validJobs;
    }

    public List<JobInfo> getJobInfoList() {
        return jobDao.readActiveJobs();
    }

    /**
     * Scans project for classes of type {@link Job} with @{@link BrainJob} annotation,
     * and maps it to its {@link BrainJob#code()}.
     *
     * @return Map with 'job code' as key, and 'job class' as value.
     */
    private Map<String, Class<?>> getJobMap() {
        log.info("scanning project classes for @BrainJob annotation");
        Map<String, Class<?>> jobMap = new HashMap<>();

        UamUtils.scanClassesWithAnnotation(JOB_BASE_PACKAGE, BrainJob.class).stream()
                .filter(Job.class::isAssignableFrom)
                .forEach(jobClass -> {
                    String jobCode = jobClass.getAnnotation(BrainJob.class).code();
                    jobMap.put(jobCode, jobClass);
                });

        log.info("found {} classes with @BrainJob annotation", jobMap.size());
        return jobMap;
    }
}
