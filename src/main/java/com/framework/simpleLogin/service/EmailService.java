package com.framework.simpleLogin.service;

import com.framework.simpleLogin.mail.Email;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

    public String sendMail(Email details, Boolean isHtml) {
        details.setIsHtml(isHtml);

        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);
            setEmailDetails.accept(messageHelper, details);

            javaMailSender.send(mailMessage);

            return "Mail Sent Successfully...";
        } catch (MessagingException e) {
            return "Error while sending Mail...\n" + e.getMessage();
        }
    }

    public String sendTemplateMail(Email details, Map<String, Object> variables) {
        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);

            Context context = new Context();
            context.setVariables(variables);
            String template = templateEngine.process("mail/CaptchaHTML.html", context);

            details.setMsgBody(template, true);
            setEmailDetails.accept(messageHelper, details);

            javaMailSender.send(mailMessage);

            return "Mail Sent Successfully...";
        } catch (MessagingException e) {
            return "Error while sending Mail...\n" + e.getMessage();
        }
    }
}
