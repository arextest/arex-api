package com.arextest.web.core.business.util;


import com.arextest.web.common.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailUtils {

    @Value("${arex.email.domain}")
    private String arexEmailDomain;

    private String sendEmailUrl;

    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public boolean sendEmail(String mailBox, String subject, String htmlMsg, int type) {
        if (StringUtils.isEmpty(sendEmailUrl)) {
            sendEmailUrl = arexEmailDomain + "/email/sendEmail";
        }
        try {
            SendMailRequest request = new SendMailRequest(mailBox, subject, htmlMsg, CONTENT_TYPE, type);
            ResponseEntity<SendMailResponse> response = HttpUtils.post(sendEmailUrl, request, SendMailResponse.class);
            return response.getBody().getData().getSuccess();
        } catch (Exception e) {
            LOGGER.error("Failed to send email. type:{}", type);
            return false;
        }
    }

    @Data
    public static class SendMailRequest {
        private String to;
        private String subject;
        private String body;
        private String contentType;
        private int type;

        public SendMailRequest(String to, String subject, String body, String contentType, int type) {
            this.to = to;
            this.subject = subject;
            this.body = body;
            this.contentType = contentType;
            this.type = type;
        }
    }


    @Data
    public static class SendMailResponse {
        private Body data;
    }


    @Data
    public static class Body {
        private Boolean success;
    }
}
