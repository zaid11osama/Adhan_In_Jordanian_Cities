package com.arabbank.hdf.uam.brain.orchestrator;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.arabbank.hdf.uam.brain.orchestrator.SchedulingService.BRAIN_JOBS_GROUP_NAME;

@Service
@RequiredArgsConstructor
public class ManualTriggerService {
    private final Scheduler scheduler;
    private final Map<JobKey, Date> lastTriggerTimeMap = new ConcurrentHashMap<>();
    @Value("${uam.validation.minutes-between-remote-triggers:60}")
    private int minutesBetweenRemoteTriggers;

    public void triggerJobs() {
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(BRAIN_JOBS_GROUP_NAME));
            triggerJobs(jobKeys.stream()
                    .map(Key::getName)
                    .collect(Collectors.toList())
            );
        } catch (SchedulerException e) {
            throw new BrainJobTriggeringException(e);
        }
    }

    public void triggerJobs(@NotNull List<String> jobCodes) {
        List<JobKey> jobKeys = jobCodes.stream()
                .map(jobCode -> new JobKey(jobCode, BRAIN_JOBS_GROUP_NAME))
                .collect(Collectors.toList());


        if (jobKeys.stream().anyMatch(jobKey -> getStatus(jobKey) != JobStatus.IDLE)) {
            throw new BrainJobTriggeringException("There exists jobs that are running or on hold");
        }

        try {
            for (JobKey jobKey : jobKeys) {
                scheduler.triggerJob(jobKey);
                lastTriggerTimeMap.put(jobKey, new Date());
            }
        } catch (SchedulerException e) {
            throw new BrainJobTriggeringException(e);
        }
    }


    JobStatus getStatus(JobKey jobKey) {
        try {
            if (isRunning(jobKey)) {
                return JobStatus.RUNNING;
            }

            if (isOnHold(jobKey)) {
                return JobStatus.ON_HOLD;
            }

            return JobStatus.IDLE;
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isRunning(JobKey jobKey) throws SchedulerException {
        return scheduler.getCurrentlyExecutingJobs().stream()
                .anyMatch(jobCtx -> jobCtx.getJobDetail().getKey().equals(jobKey));
    }

    private boolean isOnHold(JobKey jobKey) throws SchedulerException {
        Date lastExecutionTime = getLastExecutionTime(jobKey);

        if (lastExecutionTime == null) {
            return false;
        }

        return Duration.between(lastExecutionTime.toInstant(), Instant.now())
                .minusMinutes(minutesBetweenRemoteTriggers).isNegative();
    }
    public Date getLastExecutionTime(JobKey jobKey) throws SchedulerException {
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

        Date maxDate = triggers.stream()
                .map(Trigger::getPreviousFireTime)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);

        Date lastManualRun = lastTriggerTimeMap.get(jobKey);

        if (maxDate == null)
            return lastManualRun;

        if (lastManualRun == null)
            return maxDate;

        return lastManualRun.compareTo(maxDate) > 0 ? lastManualRun : maxDate;
    }

    List<JobStatusDto> getJobStatusList() {
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(BRAIN_JOBS_GROUP_NAME));

            return jobKeys.stream()
                    .map(jobKey -> JobStatusDto.builder()
                            .jobCode(jobKey.getName())
                            .status(getStatus(jobKey))
                            .build())
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
