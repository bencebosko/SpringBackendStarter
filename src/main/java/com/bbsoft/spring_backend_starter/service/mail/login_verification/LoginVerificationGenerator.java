package com.bbsoft.spring_backend_starter.service.mail.login_verification;

import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.constant.TemplateKeys;
import com.bbsoft.spring_backend_starter.constant.TemplateVariables;
import com.bbsoft.spring_backend_starter.service.TranslationService;
import com.bbsoft.spring_backend_starter.service.mail.MailGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Component
public class LoginVerificationGenerator extends MailGenerator {

    @Autowired
    public LoginVerificationGenerator(TranslationService translationService, TemplateEngine templateEngine) {
        super(translationService, templateEngine);
    }

    @Override
    public MailType mailType() {
        return MailType.LOGIN_VERIFICATION;
    }

    @Override
    protected void setAdditionalContext(Context context, Locale locale) {
        context.setVariable(TemplateVariables.PARAGRAPH_FIRST, translationService.translate(TemplateKeys.firstParagraph(mailType()), locale));
        context.setVariable(TemplateVariables.PARAGRAPH_SECOND, translationService.translate(TemplateKeys.secondParagraph(mailType()), locale));
    }
}
