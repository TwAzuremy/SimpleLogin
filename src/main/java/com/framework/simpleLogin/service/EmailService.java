package com.framework.simpleLogin.service;

import com.framework.simpleLogin.mail.Email;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;
import java.util.function.BiConsumer;

@Service
public class EmailService {
    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private TemplateEngine templateEngine;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.mail.username}")
    private String sender;

    private final BiConsumer<MimeMessageHelper, Email> setEmailDetails = (helper, details) -> {
        try {
            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setText(details.getMsgBody(), details.getIsHtml());
            helper.setSubject(details.getSubject());

            if (details.getAttachment() != null) {
                File file = new File(details.getAttachment());

                if (file.exists()) {
                    FileSystemResource resource = new FileSystemResource(file);
                    helper.addAttachment(file.getName(), resource);
                }
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    };

    private final BiConsumer<String, Object> mailLogger = (recipient, content) -> {
        if (content instanceof Exception e) {
            // Output when an email fails to be sent.
            logger.error("Mail messages from {} to {} failed to be sent with the following error: \n{}", sender, recipient, e.getMessage());
        } else {
            // Output when an email is successfully sent.
            logger.info("A mail message was successfully sent from {} to {}. The content is: \n{}", sender, recipient, content);
        }
    };

    public Boolean sendMail(Email details, Boolean isHtml) {
        details.setIsHtml(isHtml);

        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);
            setEmailDetails.accept(messageHelper, details);

            javaMailSender.send(mailMessage);

            mailLogger.accept(details.getRecipient(), details.getMsgBody());

            return true;
        } catch (MessagingException e) {
            mailLogger.accept(details.getRecipient(), e);

            return false;
        }
    }

    public Boolean sendTemplateMail(Email details, Map<String, Object> variables) {
        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);

            Context context = new Context();
            context.setVariables(variables);
            String template = templateEngine.process("mail/CaptchaHTML.html", context);

            details.setMsgBody(template, true);
            setEmailDetails.accept(messageHelper, details);

            javaMailSender.send(mailMessage);

            mailLogger.accept(details.getRecipient(), details.getMsgBody());

            return true;
        } catch (MessagingException e) {
            mailLogger.accept(details.getRecipient(), e);

            return false;
        }
    }
}
