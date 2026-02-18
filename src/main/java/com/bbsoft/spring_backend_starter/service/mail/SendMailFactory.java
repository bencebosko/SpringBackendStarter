package com.bbsoft.spring_backend_starter.service.mail;

import com.bbsoft.spring_backend_starter.constant.MailContent;
import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.constant.TemplateVariables;

import com.bbsoft.spring_backend_starter.service.dto.UserAuthDetails;
import com.bbsoft.spring_backend_starter.service.dto.mail.SendMailDTO;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class SendMailFactory {

    public SendMailDTO createLoginVerificationMail(UserAuthDetails userAuthDetails, int verificationCode, Locale locale) {
        return SendMailDTO.builder()
            .mailType(MailType.LOGIN_VERIFICATION)
            .toAddress(userAuthDetails.getEmail())
            .firstName(userAuthDetails.getFirstName())
            .locale(locale)
            .templateVariables(Map.of(
                TemplateVariables.USERNAME, userAuthDetails.getUsername(),
                TemplateVariables.VERIFICATION_CODE, verificationCode))
            .subjectReplacements(new Object[]{verificationCode})
            .mailContents(Set.of(MailContent.LOGO))
            .build();
    }
}
