package com.bbsoft.spring_backend_starter.service.mail.login_verification;

import com.bbsoft.spring_backend_starter.constant.MailContent;
import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.entity.Mail;
import com.bbsoft.spring_backend_starter.service.mail.MailSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class LoginVerificationSchedulerTest {

    private static final MailType MAIL_TYPE = MailType.LOGIN_VERIFICATION;
    private static final String TO_ADDRESS = "toAddress";
    private static final String SUBJECT = "subject";
    private static final String HTML_BODY = "htmlBody";
    private static final Set<MailContent> MAIL_CONTENTS = Set.of(MailContent.LOGO);

    @Mock
    private MailRepository mailRepository;
    @Mock
    private MailSenderService mailSenderService;
    @InjectMocks
    private LoginVerificationScheduler loginVerificationScheduler;

    @Test
    public void sendLoginVerificationMails() {
        // GIVEN
        var firstMail = createMail();
        var secondMail = createMail();
        var removedMails = List.of(firstMail, secondMail);
        Mockito.when(mailRepository.removeAllByMailType(MailType.LOGIN_VERIFICATION.name())).thenReturn(removedMails);
        // WHEN
        loginVerificationScheduler.sendLoginVerificationMails();
        // THEN
        Mockito.verify(mailRepository, Mockito.times(1)).removeAllByMailType(MAIL_TYPE.name());
        Mockito.verify(mailSenderService, Mockito.times(2)).sendMail(TO_ADDRESS, SUBJECT, HTML_BODY, MAIL_CONTENTS);
    }

    private Mail createMail() {
        return Mail.builder()
            .mailType(MAIL_TYPE)
            .toAddress(TO_ADDRESS)
            .subject(SUBJECT)
            .htmlBody(HTML_BODY)
            .mailContents(MAIL_CONTENTS)
            .build();
    }
}
