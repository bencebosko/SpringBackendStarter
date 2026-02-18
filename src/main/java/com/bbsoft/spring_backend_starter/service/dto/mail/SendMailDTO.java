package com.bbsoft.spring_backend_starter.service.dto.mail;

import com.bbsoft.spring_backend_starter.constant.MailContent;
import com.bbsoft.spring_backend_starter.constant.MailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMailDTO {

    private MailType mailType;
    private String toAddress;
    private String firstName;
    private Locale locale;
    @Builder.Default
    private Map<String, Object> templateVariables = new HashMap<>();
    private Object[] subjectReplacements;
    @Builder.Default
    private Set<MailContent> mailContents = new HashSet<>();
}
