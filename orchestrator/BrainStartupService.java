package com.arabbank.hdf.uam.brain.orchestrator;

import com.arabbank.hdf.uam.brain.mail.EmailItemCode;
import com.arabbank.hdf.uam.brain.mail.MailingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrainStartupService {
    private static final Logger log = LoggerFactory.getLogger(BrainStartupService.class);
    private final SchedulingService schedulingService;
    private final ManualTriggerService manualTriggerService;
    private final MailingService mailingService;

    @Value("${uam.validation.run-cycle-on-start}")
    private boolean runValidationCycleOnStart;

    @Value("${uam.validation.schedule-jobs-on-start:true}")
    private boolean scheduleValidationJobsOnStart;

    @Value("${uam.mail.send-test-email-on-startup:false}")
    private boolean sendTestEmailOnStartup;


    public void onStartup() {
        if (sendTestEmailOnStartup) {
            log.info("Sending test email on start");
            testEmail();
        }

        if (scheduleValidationJobsOnStart) {
            schedulingService.scheduleTasks();
        }

        if (runValidationCycleOnStart) {
            log.info("Triggering validation cycle on start");

            try {
                manualTriggerService.triggerJobs();
            } catch (BrainJobTriggeringException e) {
                log.error("Failed to trigger validation cycle", e);
            }
        }
    }

    private void testEmail() {
        try {
            mailingService.sendEmail(
                    EmailItemCode.UAM_TEST,
                    "UAM - Test email",
                    "This is a test email sent from the new UAM system. Please ignore."
            );
            log.info("Test email was sent successfully.");

        } catch (Exception e) {
            log.error("Failed to send test email", e);
        }
    }
}
