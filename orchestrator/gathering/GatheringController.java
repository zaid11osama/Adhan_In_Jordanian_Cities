package com.arabbank.hdf.uam.brain.orchestrator.gathering;

import com.arabbank.hdf.uam.brain.orchestrator.JobStatusDto;
import com.arabbank.hdf.uam.brain.orchestrator.SchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/collector/jobs")
@RequiredArgsConstructor
public class GatheringController {
    private final CollectorClient collectorClient;
    private final SchedulingService schedulingService;

    @GetMapping("/run")
    public ResponseEntity<String> runActiveJobs(@RequestParam(value = "codes", required = false) List<String> jobCodes) {
        if (jobCodes == null || jobCodes.isEmpty()) {
            return new ResponseEntity<>(collectorClient.callRunCycleApi());
        }

        return new ResponseEntity<>(collectorClient.callRunCycleApi(jobCodes.toArray(new String[0])));
    }

    @GetMapping("/status")
    public ResponseEntity<List<JobStatusDto>> getJobStatusList() {
        return ResponseEntity.ok(collectorClient.callGetCycleStatusApi());
    }

    @GetMapping("/reschedule")
    public ResponseEntity<String> rescheduleActions() {
        schedulingService.scheduleTasks();
        collectorClient.callRescheduleActions();
        return ResponseEntity.ok("Actions rescheduled successfully.");
    }
}
