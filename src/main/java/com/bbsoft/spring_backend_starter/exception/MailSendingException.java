package com.bbsoft.spring_backend_starter.exception;

import com.bbsoft.spring_backend_starter.constant.ErrorCodes;
import org.springframework.http.HttpStatus;

public class MailSendingException extends SpringBackendException {

    public MailSendingException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.MAIL_SENDING_ERROR, message);
    }
}
