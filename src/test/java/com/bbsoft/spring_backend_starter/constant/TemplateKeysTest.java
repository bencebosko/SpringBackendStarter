package com.bbsoft.spring_backend_starter.constant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TemplateKeysTest {

    private static final MailType MAIL_TYPE = MailType.LOGIN_VERIFICATION;

    @Test
    public void subject() {
        // GIVEN
        var expectedTranslation = "mail." + MAIL_TYPE.getTemplateName() + ".subject.subject";
        // THEN
        Assertions.assertEquals(expectedTranslation, TemplateKeys.subject(MAIL_TYPE));
    }

    @Test
    public void firstParagraph() {
        // GIVEN
        var expectedTranslation = "mail." + MAIL_TYPE.getTemplateName() + ".body.first-paragraph";
        // THEN
        Assertions.assertEquals(expectedTranslation, TemplateKeys.firstParagraph(MAIL_TYPE));
    }

    @Test
    public void secondParagraph() {
        // GIVEN
        var expectedTranslation = "mail." + MAIL_TYPE.getTemplateName() + ".body.second-paragraph";
        // THEN
        Assertions.assertEquals(expectedTranslation, TemplateKeys.secondParagraph(MAIL_TYPE));
    }
}
