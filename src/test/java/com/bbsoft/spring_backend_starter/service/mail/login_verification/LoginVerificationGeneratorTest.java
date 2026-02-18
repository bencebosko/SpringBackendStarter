package com.bbsoft.spring_backend_starter.service.mail.login_verification;

import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.constant.TemplateKeys;
import com.bbsoft.spring_backend_starter.constant.TemplateVariables;
import com.bbsoft.spring_backend_starter.service.TranslationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class LoginVerificationGeneratorTest {

    private static final MailType MAIL_TYPE = MailType.LOGIN_VERIFICATION;

    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private TranslationService translationService;
    @InjectMocks
    private LoginVerificationGenerator loginVerificationGenerator;

    @Test
    public void generate_ShouldCreateContext() {
        // GIVEN
        var firstName = "firstName";
        var locale = Locale.US;
        var username = "username";
        var verificationCode = 123456;
        Map<String, Object> templateVariables = Map.of(TemplateVariables.USERNAME, username, TemplateVariables.VERIFICATION_CODE, verificationCode);
        var expectedGreetings = "Greetings";
        var expectedFirstParagraph = "First paragraph";
        var expectedSecondParagraph = "Second paragraph";
        var expectedGoodbye = "Goodbye";
        var expectedSignature = "Signature";
        Mockito.when(translationService.translate(TemplateKeys.GREETINGS, locale, firstName)).thenReturn(expectedGreetings);
        Mockito.when(translationService.translate(TemplateKeys.firstParagraph(MAIL_TYPE), locale)).thenReturn(expectedFirstParagraph);
        Mockito.when(translationService.translate(TemplateKeys.secondParagraph(MAIL_TYPE), locale)).thenReturn(expectedSecondParagraph);
        Mockito.when(translationService.translate(TemplateKeys.GOODBYE, locale)).thenReturn(expectedGoodbye);
        Mockito.when(translationService.translate(TemplateKeys.SIGNATURE, locale)).thenReturn(expectedSignature);
        // WHEN
        loginVerificationGenerator.generate(firstName, locale, templateVariables);
        // THEN
        final var contextCaptor = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(templateEngine, Mockito.times(1)).process(eq(MAIL_TYPE.getTemplateName()), contextCaptor.capture());
        Assertions.assertEquals(expectedGreetings, contextCaptor.getValue().getVariable(TemplateVariables.GREETINGS));
        Assertions.assertEquals(expectedGoodbye, contextCaptor.getValue().getVariable(TemplateVariables.GOODBYE));
        Assertions.assertEquals(expectedSignature, contextCaptor.getValue().getVariable(TemplateVariables.SIGNATURE));
        Assertions.assertEquals(expectedFirstParagraph, contextCaptor.getValue().getVariable(TemplateVariables.PARAGRAPH_FIRST));
        Assertions.assertEquals(expectedSecondParagraph, contextCaptor.getValue().getVariable(TemplateVariables.PARAGRAPH_SECOND));
        Assertions.assertEquals(username, contextCaptor.getValue().getVariable(TemplateVariables.USERNAME));
        Assertions.assertEquals(verificationCode, contextCaptor.getValue().getVariable(TemplateVariables.VERIFICATION_CODE));
    }
}
