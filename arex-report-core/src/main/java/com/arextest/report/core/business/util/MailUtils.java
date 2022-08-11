package com.arextest.report.core.business.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MailUtils {

    private static final String EMAIL_NAME = "ArexTest";

    @Value("${arex.report.email.host}")
    private String emailHost;
    @Value("${arex.report.email.from}")
    private String emailFrom;
    @Value("${arex.report.email.pwd}")
    private String emailPassword;

    public boolean sendEmail(String mailBox, String subject, String htmlMsg, boolean isSSL) {
        if (StringUtils.isEmpty(mailBox)) {
            LOGGER.error("email address is empty");
        }
        try {
            HtmlEmail email = new HtmlEmail();
            List<String> list = new ArrayList<>();
            list.add(mailBox);
            String[] tos = list.toArray(new String[list.size()]);

            email.setHostName(emailHost);
            if (isSSL) {
                email.setSSLOnConnect(true);
                email.setSmtpPort(465);
            }
            // email.setCharset("UTF-8");
            email.addTo(tos);
            email.setFrom(emailFrom, EMAIL_NAME);
            email.setAuthentication(emailFrom, emailPassword);
            email.setSubject(subject);
            email.setHtmlMsg(htmlMsg);

            email.send();
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to send email", e);
        }
        return false;
    }
}
