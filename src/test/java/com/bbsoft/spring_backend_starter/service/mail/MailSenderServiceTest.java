package com.bbsoft.spring_backend_starter.service.mail;

import com.bbsoft.spring_backend_starter.config.properties.MailProperties;
import com.bbsoft.spring_backend_starter.config.providers.MessageHelperProvider;
import com.bbsoft.spring_backend_starter.constant.MailContent;
import com.bbsoft.spring_backend_starter.exception.MailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Properties;
import java.util.Set;

import static com.bbsoft.spring_backend_starter.service.mail.MailSenderService.MAIL_ENCODING;
import static com.bbsoft.spring_backend_starter.service.mail.MailSenderService.MULTIPART;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MailSenderServiceTest {

    private static final String TO_ADDRESS = "toAddress";
    private static final String FROM_ADDRESS = "fromAddress";
    private static final String SUBJECT = "subject";
    private static final String HTML_BODY = "htmlBody";
    private static final MailContent MAIL_CONTENT = MailContent.LOGO;
    private static final Set<MailContent> MAIL_CONTENTS = Set.of(MAIL_CONTENT);

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MessageHelperProvider messageHelperProvider;
    @Mock
    private MailProperties mailProperties;
    @InjectMocks
    private MailSenderService mailSenderService;
    private MimeMessage mimeMessage;
    private MimeMessageHelper mimeMessageHelper;

    @BeforeEach
    public void initMocks() throws MessagingException {
        mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        mimeMessageHelper = Mockito.spy(new MimeMessageHelper(mimeMessage, MULTIPART, MAIL_ENCODING));
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        Mockito.when(messageHelperProvider.getMessageHelper(mimeMessage, MULTIPART, MAIL_ENCODING)).thenReturn(mimeMessageHelper);
        Mockito.lenient().when(mailProperties.getFromAddress()).thenReturn(FROM_ADDRESS);
    }

    @Test
    public void sendMail_ShouldSendTheCorrectMessage() throws MessagingException {
        // WHEN
        mailSenderService.sendMail(TO_ADDRESS, SUBJECT, HTML_BODY, MAIL_CONTENTS);
        // THEN
        Mockito.verify(mimeMessageHelper, times(1)).setTo(TO_ADDRESS);
        Mockito.verify(mimeMessageHelper, times(1)).setFrom(FROM_ADDRESS);
        Mockito.verify(mimeMessageHelper, times(1)).setSubject(SUBJECT);
        Mockito.verify(mimeMessageHelper, times(1)).setText(HTML_BODY, MailSenderService.HTML_CONTENT);
        Mockito.verify(mimeMessageHelper, times(1)).addInline(MAIL_CONTENT.getContentId(), new ClassPathResource(MAIL_CONTENT.getPathFromResources()));
        Mockito.verify(mailSender, times(1)).send(mimeMessage);

        Assertions.assertEquals(MAIL_ENCODING, mimeMessageHelper.getEncoding());
        Assertions.assertEquals(MULTIPART, mimeMessageHelper.isMultipart());
    }

    @Test
    public void sendMail_ShouldThrowMailSendingExceptionForMessagingException() throws MessagingException {
        // GIVEN
        Mockito.when(messageHelperProvider.getMessageHelper(mimeMessage, MULTIPART, MAIL_ENCODING)).thenThrow(new MessagingException());
        // THEN
        Assertions.assertThrows(MailSendingException.class, () -> mailSenderService.sendMail(TO_ADDRESS, SUBJECT, HTML_BODY, MAIL_CONTENTS));
    }

    @Test
    public void sendMail_ShouldThrowMailSendingExceptionForMailSendException() {
        // GIVEN
        var mailSendException = new MailSendException("message");
        doThrow(mailSendException).when(mailSender).send(mimeMessage);
        // THEN
        Assertions.assertThrows(MailSendingException.class, () -> mailSenderService.sendMail(TO_ADDRESS, SUBJECT, HTML_BODY, MAIL_CONTENTS));
    }
}
