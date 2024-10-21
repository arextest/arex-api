package com.arextest.web.core.business.oauth.impl;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.beans.httpclient.HttpWebServiceApiClient;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/8/15
 */
@Slf4j
@Component("GithubOauth")
public class GithubOauthServiceImpl extends AbstractOauthServiceImpl {

  private static final String GITHUB_ACCESS_TOKEN_URL =
      "https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s";
  private static final String GITHUB_USER_URL = "https://api.github.com/user";
  private static final String TOKEN = "token ";
  private static final String ACCEPT = "Accept";

  @Value("${arex.oauth.github.clientid}")
  private String clientId;
  @Value("${arex.oauth.github.secret}")
  private String secret;

  @Resource
  private HttpWebServiceApiClient httpWebServiceApiClient;

  @Override
  public String getClientId() {
    return clientId;
  }

  @Override
  public String getOauthUri() {
    return null;
  }

  @Override
  public String getUser(String code, String redirectUri) {
    if (!checkOauth(clientId, secret, code)) {
      return null;
    }
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.add(ACCEPT, APPLICATION_JSON);
      headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
      String url = String.format(GITHUB_ACCESS_TOKEN_URL, clientId, secret, code);
      Map tokenResponse =
          httpWebServiceApiClient.get(
              url, Collections.emptyMap(), headers, TIMEOUT,
              Map.class);
      Map<String, String> tokenBody = Objects.requireNonNull(tokenResponse);
      String accessToken = tokenBody.get(ACCESS_TOKEN);

      headers = new HttpHeaders();
      headers.add(AUTHORIZATION, TOKEN + accessToken);
      headers.add(ACCEPT, APPLICATION_JSON);
      headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
      Map result = httpWebServiceApiClient.get(GITHUB_USER_URL,
          Collections.emptyMap(),
          headers, TIMEOUT,
          Map.class);
      Map<String, String> body = Objects.requireNonNull(result);
      return body.get(EMAIL);
    } catch (Exception e) {
      LogUtils.error(LOGGER, "get github user error", e);
      return null;
    }
  }
}
