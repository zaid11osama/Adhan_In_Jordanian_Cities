package com.arabbank.hdf.uam.brain.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class ValidationController {
    private final ManualTriggerService manualTriggerService;

    @GetMapping("/run")
    public ResponseEntity<String> runActiveJobs(@RequestParam(value = "codes", required = false) List<String> jobCodes) {
        if (jobCodes == null || jobCodes.isEmpty()) {
            manualTriggerService.triggerJobs();
            return ResponseEntity.ok("Brain jobs triggered successfully.");
        }

        manualTriggerService.triggerJobs(jobCodes);
        return ResponseEntity.ok("Brain jobs triggered successfully.");
    }

    @GetMapping("/status")
    public ResponseEntity<List<JobStatusDto>> getJobStatusList() {
        return ResponseEntity.ok(manualTriggerService.getJobStatusList());
    }
}
