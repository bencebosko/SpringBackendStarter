package com.bbsoft.spring_backend_starter.service.mail;

import com.bbsoft.spring_backend_starter.constant.MailContent;
import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.constant.TemplateKeys;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.entity.Mail;
import com.bbsoft.spring_backend_starter.service.TranslationService;
import com.bbsoft.spring_backend_starter.service.dto.mail.SendMailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MailService {

    private final List<MailGenerator> mailGenerators;
    private final MailRepository mailRepository;
    private final TranslationService translationService;

    public void saveMail(SendMailDTO sendMailDTO) {
        getMailGenerator(sendMailDTO.getMailType()).ifPresentOrElse(mailGenerator -> {
            var mailType = sendMailDTO.getMailType();
            var toAddress = sendMailDTO.getToAddress();
            var locale = sendMailDTO.getLocale();
            var subjectReplacements = sendMailDTO.getSubjectReplacements();
            var mailContents = sendMailDTO.getMailContents();
            var generatedHtml = mailGenerator.generate(sendMailDTO.getFirstName(), sendMailDTO.getLocale(), sendMailDTO.getTemplateVariables());
            mailRepository.save(convertToMail(mailType, toAddress, locale, generatedHtml, subjectReplacements, mailContents));
            log.info("Mail {} saved with addressee {}.", mailType.getTemplateName(), sendMailDTO.getToAddress());
        }, () -> {
            log.info("MailGenerator not found for mail: {}", sendMailDTO.getMailType().getTemplateName());
        });
    }

    private Mail convertToMail(MailType mailType,
                               String toAddress,
                               Locale locale,
                               String htmlBody,
                               Object[] subjectReplacements,
                               Set<MailContent> mailContents) {
        var subjectKey = TemplateKeys.subject(mailType);
        var subject = translationService.translate(subjectKey, locale, subjectReplacements);
        return Mail.builder()
            .mailType(mailType)
            .subject(subject)
            .toAddress(toAddress)
            .htmlBody(htmlBody)
            .mailContents(mailContents)
            .build();
    }

    private Optional<MailGenerator> getMailGenerator(MailType mailType) {
        return mailGenerators.stream().filter(mailGenerator -> Objects.equals(mailGenerator.mailType(), mailType)).findFirst();
    }
}
