package com.arabbank.hdf.uam.brain.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
@Slf4j
public class MailingController {
    private final MailingService mailingService;

    @PostMapping("/send-email")
    public ResponseEntity<String> runActiveJobs(@RequestBody EmailRequest emailRequest) {
        log.info("Mail request received: {}", emailRequest);
        mailingService.sendEmail(
                emailRequest.getItemCode(),
                emailRequest.getSubject(),
                emailRequest.getText(),
                emailRequest.getTo(),
                emailRequest.getCc()
        );
        return ResponseEntity.ok("Email sent successfully.");
    }
}
