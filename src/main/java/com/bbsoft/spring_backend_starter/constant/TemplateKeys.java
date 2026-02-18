package com.bbsoft.spring_backend_starter.constant;

public final class TemplateKeys {

    private TemplateKeys() {}

    public static String GREETINGS = "mail.all.header.greetings";
    public static String GOODBYE = "mail.all.footer.goodbye";
    public static String SIGNATURE = "mail.all.footer.signature";

    private static final String MAIL_PREFIX = "mail";
    private static final String SEPARATOR = ".";

    public static String subject(MailType mailType) {
        return concat(MAIL_PREFIX, mailType.getTemplateName(), "subject.subject");
    }

    public static String firstParagraph(MailType mailType) {
        return concat(MAIL_PREFIX, mailType.getTemplateName(), "body.first-paragraph");
    }

    public static String secondParagraph(MailType mailType) {
        return concat(MAIL_PREFIX, mailType.getTemplateName(), "body.second-paragraph");
    }

    private static String concat(String... parts) {
        return String.join(SEPARATOR, parts);
    }
}
