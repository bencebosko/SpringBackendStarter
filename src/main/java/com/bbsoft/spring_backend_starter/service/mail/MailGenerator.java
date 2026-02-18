package com.bbsoft.spring_backend_starter.service.mail;

import com.bbsoft.spring_backend_starter.constant.MailType;

import com.bbsoft.spring_backend_starter.constant.TemplateVariables;
import com.bbsoft.spring_backend_starter.constant.TemplateKeys;
import com.bbsoft.spring_backend_starter.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public abstract class MailGenerator {

    protected final TranslationService translationService;
    private final TemplateEngine templateEngine;

    public abstract MailType mailType();

    public String generate(String firstName, Locale locale, Map<String, Object> templateVariables) {
        var context = createBaseContext(firstName, locale, templateVariables);
        setAdditionalContext(context, locale);
        return templateEngine.process(mailType().getTemplateName(), context);
    }

    protected abstract void setAdditionalContext(Context context, Locale locale);

    private Context createBaseContext(String firstName, Locale locale, Map<String, Object> templateVariables) {
        final var context = new Context();
        context.setVariable(TemplateVariables.GREETINGS, translationService.translate(TemplateKeys.GREETINGS, locale, firstName));
        context.setVariable(TemplateVariables.GOODBYE, translationService.translate(TemplateKeys.GOODBYE, locale));
        context.setVariable(TemplateVariables.SIGNATURE, translationService.translate(TemplateKeys.SIGNATURE, locale));
        context.setVariables(templateVariables);
        return context;
    }
}
