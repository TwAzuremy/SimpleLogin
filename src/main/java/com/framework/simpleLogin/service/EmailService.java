package com.framework.simpleLogin.service;

import com.framework.simpleLogin.annotation.Loggable;
import com.framework.simpleLogin.entity.Email;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Gadget;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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
import java.util.concurrent.ExecutionException;

@Service
public class EmailService {
    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private CaptchaService captchaService;

    @Value("${spring.mail.username}")
    private String sender;

    private void setDetails(MimeMessageHelper helper, Email email, boolean isHTML) {
        try {
            helper.setFrom(sender);
            helper.setTo(email.getRecipient());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMsgBody(), isHTML);

            // If there is an attachment, then send it.
            if (email.getAttachment() != null) {
                File file = new File(email.getAttachment());

                if (file.exists()) {
                    FileSystemResource resource = new FileSystemResource(file);
                    helper.addAttachment(file.getName(), resource);
                }
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @Loggable(recordResult = false)
    public CompletableFuture<Boolean> send(Email email, boolean isHTML) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            setDetails(helper, email, isHTML);

            javaMailSender.send(message);

            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async
    @Loggable(recordResult = false)
    public CompletableFuture<Boolean> sendByTemplate(Email email, String templatePath, Map<String, Object> variables) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            // Generate an HTML template
            Context context = new Context();
            context.setVariables(variables);
            String template = templateEngine.process(templatePath, context);

            // Save the HTML template to the msgBody.
            email.setMsgBody(template);
            setDetails(helper, email, true);

            javaMailSender.send(message);

            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async
    @Loggable(recordResult = false)
    public CompletableFuture<Boolean> sendCaptcha(String cacheName, String email, String subject, Map<String, Object> variables) throws ExecutionException, InterruptedException {
        String captcha = captchaService.get(cacheName, email);

        if (Gadget.StringUtils.isEmpty(captcha)) {
            captcha = captchaService.generate(6);
            captchaService.store(cacheName, email, captcha);
        }

        variables.put("code", captcha);

        Email details = new Email();
        details.setRecipient(email);
        details.setSubject(subject);

        boolean isSend = sendByTemplate(details, CONSTANT.OTHER.CAPTCHA_TEMPLATE, variables).get();
        return CompletableFuture.completedFuture(isSend);
    }
}
