package com.arabbank.hdf.uam.brain.mail;

import java.util.List;

public interface MailingService {
    void sendEmail(String itemCode, String subject, String text);

    void sendEmail(
            String itemCode,
            String subject,
            String text,
            List<String> to,
            List<String> cc
    );

    void sendEmail(String subject, String text, List<String> to, List<String> cc);
}
