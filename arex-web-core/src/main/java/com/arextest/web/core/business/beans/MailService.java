package com.arextest.web.core.business.beans;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.beans.httpclient.HttpWebServiceApiClient;
import javax.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailService {

  private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

  private String sendEmailUrl;

  @Value("${arex.email.domain}")
  private String arexEmailDomain;
  @Resource
  private HttpWebServiceApiClient httpWebServiceApiClient;

  public boolean sendEmail(String mailBox, String subject, String htmlMsg, int type) {
    if (StringUtils.isEmpty(sendEmailUrl)) {
      sendEmailUrl = arexEmailDomain + "/email/sendEmail";
    }
    try {
      SendMailRequest request = new SendMailRequest(mailBox, subject, htmlMsg, CONTENT_TYPE, type);
      SendMailResponse response = httpWebServiceApiClient.post(false,
          sendEmailUrl, request,
          SendMailResponse.class);
      return response.getData().getSuccess();
    } catch (Exception e) {
      LogUtils.error(LOGGER, "Failed to send email. type:{}", type);
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
