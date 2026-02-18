package com.bbsoft.spring_backend_starter.service.mail;

import com.bbsoft.spring_backend_starter.constant.MailContent;
import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.constant.TemplateKeys;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.entity.Mail;
import com.bbsoft.spring_backend_starter.service.TranslationService;
import com.bbsoft.spring_backend_starter.service.dto.mail.SendMailDTO;
import com.bbsoft.spring_backend_starter.service.mail.login_verification.LoginVerificationGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    private static final MailType MAIL_TYPE = MailType.LOGIN_VERIFICATION;
    private static final String TO_ADDRESS = "toAddress";
    private static final String FIRST_NAME = "firstName";
    private static final Locale LOCALE = Locale.US;
    private static final Map<String, Object> TEMPLATE_VARIABLES = Map.of("name", "value");
    private static final Object[] SUBJECT_REPLACEMENTS = {"replacement"};
    private static final Set<MailContent> MAIL_CONTENTS = Set.of(MailContent.LOGO);

    private static final String SUBJECT = "subject";
    private static final String HTML_BODY = "htmlBody";

    @Mock
    private LoginVerificationGenerator mailGenerator;
    @Spy
    private MailRepository mailRepository;
    @Mock
    private TranslationService translationService;
    private MailService mailService;

    @BeforeEach
    public void initMocks() {
        mailService = new MailService(List.of(mailGenerator), mailRepository, translationService);
    }

    @Test
    public void saveMail_ShouldConvertAndSaveMail() {
        // GIVEN
        Mockito.when(mailGenerator.mailType()).thenReturn(MAIL_TYPE);
        Mockito.when(translationService.translate(TemplateKeys.subject(MAIL_TYPE), LOCALE, SUBJECT_REPLACEMENTS)).thenReturn(SUBJECT);
        Mockito.when(mailGenerator.generate(FIRST_NAME, LOCALE, TEMPLATE_VARIABLES)).thenReturn(HTML_BODY);
        // WHEN
        mailService.saveMail(createSendMailDTO());
        // THEN
        var mailCaptor = ArgumentCaptor.forClass(Mail.class);
        Mockito.verify(mailRepository, Mockito.times(1)).save(mailCaptor.capture());
        var mail = mailCaptor.getValue();
        Assertions.assertEquals(MAIL_TYPE, mail.getMailType());
        Assertions.assertEquals(TO_ADDRESS, mail.getToAddress());
        Assertions.assertEquals(SUBJECT, mail.getSubject());
        Assertions.assertEquals(HTML_BODY, mail.getHtmlBody());
        Assertions.assertEquals(MAIL_CONTENTS, mail.getMailContents());
    }

    private SendMailDTO createSendMailDTO() {
        return SendMailDTO.builder()
            .mailType(MAIL_TYPE)
            .toAddress(TO_ADDRESS)
            .firstName(FIRST_NAME)
            .locale(LOCALE)
            .templateVariables(TEMPLATE_VARIABLES)
            .subjectReplacements(SUBJECT_REPLACEMENTS)
            .mailContents(MAIL_CONTENTS)
            .build();
    }
}
