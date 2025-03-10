package com.arabbank.hdf.uam.brain.mail;

import com.arabbank.hdf.uam.brain.mail.exception.EmptyDetailsException;
import com.arabbank.hdf.uam.brain.mail.exception.EmptyRecipientsException;
import com.arabbank.hdf.uam.brain.mail.exception.InvalidItemCodeException;
import com.arabbank.hdf.uam.brain.mail.exception.MailingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class DefaultMailingService implements MailingService {
    private final EmailItemService emailItemService;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;


    @Value("${uam.mail.sender}")
    private String sender;

    private static final String DEFAULT_TEMPLATE = "simple.html";

    public Context getThymeleafContext(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        return context;
    }

    private String applyTemplate(String message) {
        return templateEngine.process(DEFAULT_TEMPLATE, getThymeleafContext(message));
    }

    @Override
    public void sendEmail(String itemCode, String subject, String text) {
        sendEmail(itemCode, subject, text, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public void sendEmail(
            @NotNull String itemCode,
            @NotNull String subject,
            @NotNull String text,
            @Nullable List<String> to,
            @Nullable List<String> cc
    ) {
        if (itemCode == null) {
            throw new EmptyDetailsException("Item Code not specified (Null)");
        }


        EmailItem emailConfig = getEmailConfigByItemCode(itemCode)
                .orElseThrow(() -> new InvalidItemCodeException("Could not find email item with code: " + itemCode));

        if (!emailConfig.isEnabled()) {
            return;
        }

        if (to == null) {
            to = Collections.emptyList();
        }

        if (cc == null) {
            cc = Collections.emptyList();
        }

        to = new ArrayList<>(to);
        cc = new ArrayList<>(cc);
        to.addAll(emailConfig.getToAddresses());
        cc.addAll(emailConfig.getCcAddresses());
        to.remove(null);
        cc.remove(null);
        sendEmail(subject, text, to, cc);
    }

    @Override
    public void sendEmail(String subject, String body, List<String> to, List<String> cc) {
        if (StringUtils.isAnyBlank(subject, body)) {
            throw new EmptyDetailsException("Can't send email with empty subject or body");
        }

        if (to.isEmpty() && cc.isEmpty()) {
            throw new EmptyRecipientsException("Can't send email without specifying recipients");
        }

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(sender);
            helper.setTo(to.toArray(new String[0]));
            helper.setCc(cc.toArray(new String[0]));

            helper.setSubject(subject);
            helper.setText(applyTemplate(body), true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailingException(e);
        }
    }

    private Optional<EmailItem> getEmailConfigByItemCode(String itemCode) {
        return emailItemService.getEmailItem(itemCode);
    }
}
