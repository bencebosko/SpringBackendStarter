package com.bbsoft.spring_backend_starter.service.mail;

import com.bbsoft.spring_backend_starter.config.properties.MailProperties;
import com.bbsoft.spring_backend_starter.config.providers.MessageHelperProvider;
import com.bbsoft.spring_backend_starter.constant.MailContent;
import com.bbsoft.spring_backend_starter.exception.MailSendingException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailSenderService {

    public static final String MAIL_ENCODING = "UTF-8";
    public static final boolean MULTIPART = true;
    public static final boolean HTML_CONTENT = true;

    private final JavaMailSender mailSender;
    private final MessageHelperProvider messageHelperProvider;
    private final MailProperties mailProperties;

    public void sendMail(String toAddress, String subject, String htmlBody, Set<MailContent> mailContents) {
        try {
            final var message = mailSender.createMimeMessage();
            final var messageHelper = messageHelperProvider.getMessageHelper(message, MULTIPART, MAIL_ENCODING);
            messageHelper.setTo(toAddress);
            messageHelper.setFrom(mailProperties.getFromAddress());
            messageHelper.setSubject(subject);
            messageHelper.setText(htmlBody, HTML_CONTENT);
            for (var mailContent : mailContents) {
                messageHelper.addInline(mailContent.getContentId(), new ClassPathResource(mailContent.getPathFromResources()));
            }
            mailSender.send(message);
        } catch (MessagingException | MailException ex) {
            throw new MailSendingException(ex.getMessage());
        }
    }
}
