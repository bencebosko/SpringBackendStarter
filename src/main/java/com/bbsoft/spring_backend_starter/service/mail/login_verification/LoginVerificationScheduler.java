package com.bbsoft.spring_backend_starter.service.mail.login_verification;

import com.bbsoft.spring_backend_starter.constant.MailType;
import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.service.mail.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginVerificationScheduler {

    private final MailRepository mailRepository;
    private final MailSenderService mailSenderService;

    @Scheduled(cron = "${spring-backend.mail.login-verification-processing-cron}")
    @Transactional
    public void sendLoginVerificationMails() {
        mailRepository.removeAllByMailType(MailType.LOGIN_VERIFICATION.name()).forEach(mail -> {
            var toAddress = mail.getToAddress();
            mailSenderService.sendMail(toAddress, mail.getSubject(), mail.getHtmlBody(), mail.getMailContents());
            log.info("Mail {} has been sent to {}.", mail.getMailType().getTemplateName(), toAddress);
        });
    }
}
