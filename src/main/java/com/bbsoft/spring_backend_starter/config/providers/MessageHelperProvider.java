package com.bbsoft.spring_backend_starter.config.providers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MessageHelperProvider {

    public MimeMessageHelper getMessageHelper(MimeMessage message, boolean isMultipart, String mailEncoding) throws MessagingException {
        return new MimeMessageHelper(message, isMultipart, mailEncoding);
    }
}
