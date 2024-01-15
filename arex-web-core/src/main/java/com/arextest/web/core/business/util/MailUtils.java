package com.arextest.web.core.business.util;

import com.arextest.web.common.LogUtils;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class MailUtils {

  private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
  @Value("${arex.email.domain}")
  private String arexEmailDomain;
  private String sendEmailUrl;

  public boolean sendEmail(String mailBox, String subject, String htmlMsg, int type) {
    if (StringUtils.isEmpty(sendEmailUrl)) {
      sendEmailUrl = arexEmailDomain + "/email/sendEmail";
    }
    try {
      sendEmailUrl = "http://mail.arextest.com/email/sendEmail";
      SendMailRequest request = new SendMailRequest(mailBox, subject, htmlMsg, CONTENT_TYPE, type);
//      ResponseEntity<SendMailResponse> response = HttpUtils.post(sendEmailUrl, request,
//          SendMailResponse.class);
      SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

      Proxy proxy = new Proxy(Type.HTTP,
          new InetSocketAddress("ntproxy.qa.nt.ctripcorp.com", 8080));
      requestFactory.setProxy(proxy);

      RestTemplate restTemplate = new RestTemplate(requestFactory);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
      HttpEntity<SendMailRequest> requestEntity = new HttpEntity<>(request, httpHeaders);
      ResponseEntity<SendMailResponse> exchange = restTemplate.exchange(sendEmailUrl,
          HttpMethod.POST, requestEntity, SendMailResponse.class);
      return exchange.getBody().getData().getSuccess();
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
