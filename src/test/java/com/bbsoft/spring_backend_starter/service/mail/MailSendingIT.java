package com.bbsoft.spring_backend_starter.service.mail;

import com.bbsoft.spring_backend_starter.IntegrationTestBase;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.UserAuthRepository;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.UserRoleRepository;
import com.bbsoft.spring_backend_starter.repository.UserSettingsRepository;
import com.bbsoft.spring_backend_starter.service.TranslationService;
import com.bbsoft.spring_backend_starter.service.dto.UserAuthDetails;
import com.bbsoft.spring_backend_starter.service.mail.login_verification.LoginVerificationScheduler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Locale;

public class MailSendingIT extends IntegrationTestBase {

    private static final String USERNAME = "username";
    private static final String TO_ADDRESS = "test@gmail.com";
    private static final String FIRST_NAME = "firstName";

    private final MailService mailService;
    private final LoginVerificationScheduler loginVerificationScheduler;
    private final TranslationService translationService;
    private final SendMailFactory sendMailFactory = new SendMailFactory();
    @MockitoSpyBean
    private final JavaMailSender mailSender;

    @Autowired
    public MailSendingIT(UserRepository userRepository,
                         UserRoleRepository userRoleRepository,
                         UserAuthRepository userAuthRepository,
                         UserSettingsRepository userSettingsRepository,
                         MailRepository mailRepository,
                         MailService mailService,
                         LoginVerificationScheduler loginVerificationScheduler,
                         TranslationService translationService,
                         JavaMailSender mailSender) {
        super(userRepository, userRoleRepository, userAuthRepository, userSettingsRepository, mailRepository);
        this.mailService = mailService;
        this.loginVerificationScheduler = loginVerificationScheduler;
        this.translationService = translationService;
        this.mailSender = mailSender;
    }

    @BeforeEach
    public void loadTranslations() {
        translationService.loadTranslations();
    }

    @Test
    public void sendLoginVerificationMails_ShouldSendAllLoginVerificationMails() throws MessagingException {
        // GIVEN
        var verificationCode = 123456;
        var locale = Locale.US;
        var sendMailDTO = sendMailFactory.createLoginVerificationMail(createUserAuthDetails(), verificationCode, locale);
        mailService.saveMail(sendMailDTO);
        mailService.saveMail(sendMailDTO);
        // WHEN
        loginVerificationScheduler.sendLoginVerificationMails();
        // THEN
        var messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.verify(mailSender, Mockito.times(2)).send(messageCaptor.capture());
        var messages = messageCaptor.getAllValues();
        Assertions.assertEquals(TO_ADDRESS, messages.getFirst().getAllRecipients()[0].toString());
        Assertions.assertEquals(TO_ADDRESS, messages.get(1).getAllRecipients()[0].toString());
        Assertions.assertTrue(mailRepository.findAll().isEmpty());
    }

    private UserAuthDetails createUserAuthDetails() {
        return UserAuthDetails.builder()
            .username(USERNAME)
            .email(TO_ADDRESS)
            .firstName(FIRST_NAME)
            .build();
    }
}
