package com.arextest.report.core.business.util;


import com.arextest.report.core.business.config.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MailUtils {

    private static final String EMAIL_NAME = "ArexTest";

    @Resource
    private ApplicationProperties applicationProperties;

    public boolean sendEmail(String mailBox, String subject, String htmlMsg, boolean isSSL) {
        if (StringUtils.isEmpty(mailBox)) {
            LOGGER.error("email address is empty");
        }
        try {
            HtmlEmail email = new HtmlEmail();
            List<String> list = new ArrayList<>();
            list.add(mailBox);
            String[] tos = list.toArray(new String[list.size()]);

            email.setHostName(applicationProperties.getEmailHost());
            if (isSSL) {
                email.setSSLOnConnect(true);
                email.setSmtpPort(465);
            }
            // email.setCharset("UTF-8");
            email.addTo(tos);
            email.setFrom(applicationProperties.getEmailFrom(), EMAIL_NAME);
            email.setAuthentication(applicationProperties.getEmailFrom(), applicationProperties.getEmailPwd());
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
