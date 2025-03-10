package com.arabbank.hdf.uam.brain.orchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrainJobListener implements JobListener {
    private final JobDao jobDao;
    @Override
    public String getName() {
        return "BrainJobListener";
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException exception) {
        String jobCode = jobExecutionContext.getJobDetail().getKey().getName();
        CycleResult status = exception == null ? CycleResult.COMPLETED : CycleResult.FAILED;

        log.info("Job ({}) ended with status ({})", jobCode, exception == null ? CycleResult.COMPLETED : CycleResult.FAILED);
        if (exception != null) {
            log.error("Failed to execute job: " + jobCode, exception);
        }

        try {
            jobDao.insertJobLog(
                    jobCode,
                    status,
                    jobExecutionContext.getJobRunTime(),
                    jobExecutionContext.getFireTime(),
                    new Date(),
                    exception == null ? null : exception.getMessage()
            );
        } catch (Exception e) {
            log.error("Failed to log job result", e);
        }
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {

        log.info("Job is about to be executed: {}", jobExecutionContext.getJobDetail().getKey());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
    }
}
