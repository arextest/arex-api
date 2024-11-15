package com.arextest.web.core.business.oauth.impl;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.beans.httpclient.HttpWebServiceApiClient;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/8/18
 */
@Slf4j
@Component("GitlabOauth")
public class GitlabOauthServiceImpl extends AbstractOauthServiceImpl {

  private static final String TOKEN_SUFFIX =
      "/oauth/token?client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s";
  private static final String USER_SUFFIX = "/api/v4/user";
  private static final String BEARER = "Bearer ";
  @Value("${arex.oauth.gitlab.clientid}")
  private String clientId;
  @Value("${arex.oauth.gitlab.secret}")
  private String secret;
  @Value("${arex.oauth.gitlab.uri}")
  private String gitlabUri;
  @Resource
  private HttpWebServiceApiClient httpWebServiceApiClient;

  @Override
  public String getClientId() {
    return clientId;
  }

  @Override
  public String getOauthUri() {
    return gitlabUri;
  }

  @Override
  public String getUser(String code, String redirectUri) {
    if (!checkOauth(clientId, secret, code)) {
      return null;
    }
    if (StringUtils.isBlank(gitlabUri)) {
      LogUtils.error(LOGGER, "gitlab uri is blank");
      return null;
    }
    if (StringUtils.isBlank(redirectUri)) {
      LogUtils.error(LOGGER, "gitlab redirect uri is blank");
      return null;
    }
    String tokenUrl = String.format(gitlabUri + TOKEN_SUFFIX, clientId, secret, code, redirectUri);
    try {
      HttpHeaders headers = new HttpHeaders();
//      headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
      headers.setContentType(MediaType.APPLICATION_JSON);
      Map tokenResponse = httpWebServiceApiClient.rawPost(tokenUrl, headers);
      Map<String, String> tokenBody = Objects.requireNonNull(tokenResponse);
      LogUtils.info(LOGGER, "gitlab get user token info", tokenBody);

      headers = new HttpHeaders();
      String accessToken = tokenBody.get(ACCESS_TOKEN);
      headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
      headers.add(AUTHORIZATION, BEARER + accessToken);
      Map userResponse =
          httpWebServiceApiClient.get(gitlabUri + USER_SUFFIX,
              Collections.emptyMap(), headers, TIMEOUT,
              Map.class);
      Map<String, String> userBody = Objects.requireNonNull(userResponse);
      return userBody.get(EMAIL);
    } catch (Exception e) {
      LogUtils.error(LOGGER, "gitlab get user error info", e.getMessage());
      LogUtils.error(LOGGER, "gitlab get user error", e);
      return null;
    }
  }
}
