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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

            // If there is an attachment, then send it.
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
            logger.error("Message sent to {} failed, error message: \n{}", recipient, e.getMessage());
        } else {
            // Output when an email is successfully sent.
            logger.info("The message was successfully sent to {}. The content is: \n\t{}", recipient, content);
        }
    };

    @Async
    public CompletableFuture<Boolean> sendMail(Email details, Boolean isHtml) {
        details.setIsHtml(isHtml);

        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);
            setEmailDetails.accept(messageHelper, details);

            javaMailSender.send(mailMessage);
            mailLogger.accept(details.getRecipient(), details.getMsgBody());

            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            mailLogger.accept(details.getRecipient(), e);

            return CompletableFuture.completedFuture(false);
        }
    }

    @Async
    public CompletableFuture<Boolean> sendTemplateMail(Email details, String HTMLTemplate, Map<String, Object> variables, String logDescription) {
        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);

            // Generate an HTML template
            Context context = new Context();
            context.setVariables(variables);
            String template = templateEngine.process(HTMLTemplate, context);

            // Save the HTML template to the msgBody.
            details.setMsgBody(template, true);
            setEmailDetails.accept(messageHelper, details);

            javaMailSender.send(mailMessage);
            mailLogger.accept(details.getRecipient(), logDescription);

            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            mailLogger.accept(details.getRecipient(), e);

            return CompletableFuture.completedFuture(false);
        }
    }
}
