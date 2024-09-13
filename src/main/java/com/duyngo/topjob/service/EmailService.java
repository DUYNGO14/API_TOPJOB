package com.duyngo.topjob.service;

public interface EmailService {
    public void sendSimpleEmail();

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    public void sendEmailFromTemplateSync(String to, String subject, String templateName, String username,
            Object value);
}
