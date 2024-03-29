package com.arextest.web.core.business.oauth.impl;

import com.arextest.web.common.LogUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/8/16
 */
@Slf4j
@Component("GoogleOauth")
public class GoogleOauthServiceImpl extends AbstractOauthServiceImpl {

  private static final String EMAIL = "email";
  private static final String OFFLINE = "offline";
  @Value("${arex.oauth.google.clientid}")
  private String clientId;
  @Value("${arex.oauth.google.secret}")
  private String secret;

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
    if (StringUtils.isBlank(redirectUri)) {
      LogUtils.error(LOGGER, "google redirect uri is blank");
      return null;
    }
    try {
      HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
          jsonFactory,
          clientId, secret, Collections.singleton(EMAIL)).setAccessType(OFFLINE).build();

      GoogleAuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);
      tokenRequest.setRedirectUri(redirectUri);
      GoogleTokenResponse tokenResponse = tokenRequest.execute();

      if (StringUtils.isNotBlank(tokenResponse.getIdToken())) {
        GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(flow.getTransport(), flow.getJsonFactory()).build();

        GoogleIdToken token = verifier.verify(tokenResponse.getIdToken());
        if (token != null) {
          GoogleIdToken.Payload payload = token.getPayload();
          return payload.getEmail();
        }
      }
    } catch (Exception e) {
      LogUtils.error(LOGGER, "get google user error", e);
    }
    return null;
  }
}
