package com.bbsoft.spring_backend_starter.service.mail;

import com.bbsoft.spring_backend_starter.constant.MailContent;
import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.constant.TemplateVariables;
import com.bbsoft.spring_backend_starter.service.dto.UserAuthDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

public class SendMailFactoryTest {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "firstName";

    private final SendMailFactory sendMailFactory = new SendMailFactory();

    @Test
    public void createLoginVerificationMail_ShouldCreateSendMailDTO() {
        // GIVEN
        var userAuthDetails = createUserAuthDetails();
        var verificationCode = 123456;
        var locale = Locale.US;
        var expectedMailContents = Set.of(MailContent.LOGO);
        // WHEN
        final var sendMailDTO = sendMailFactory.createLoginVerificationMail(userAuthDetails, verificationCode, locale);
        // THEN
        Assertions.assertEquals(MailType.LOGIN_VERIFICATION, sendMailDTO.getMailType());
        Assertions.assertEquals(userAuthDetails.getEmail(), sendMailDTO.getToAddress());
        Assertions.assertEquals(userAuthDetails.getFirstName(), sendMailDTO.getFirstName());
        Assertions.assertEquals(userAuthDetails.getUsername(), sendMailDTO.getTemplateVariables().get(TemplateVariables.USERNAME));
        Assertions.assertEquals(locale, sendMailDTO.getLocale());
        Assertions.assertEquals(verificationCode, sendMailDTO.getTemplateVariables().get(TemplateVariables.VERIFICATION_CODE));
        Assertions.assertEquals(verificationCode, sendMailDTO.getSubjectReplacements()[0]);
        Assertions.assertEquals(expectedMailContents, sendMailDTO.getMailContents());
    }

    private UserAuthDetails createUserAuthDetails() {
        return UserAuthDetails.builder()
            .username(USERNAME)
            .email(EMAIL)
            .firstName(FIRST_NAME)
            .build();
    }
}
