package com.arextest.web.core.business.oauth;

/**
 * @author b_yu
 * @since 2023/8/15
 */
public interface OauthService {

  int TIMEOUT = 5000;
  String APPLICATION_JSON = "application/json";
  String AUTHORIZATION = "Authorization";
  String ACCESS_TOKEN = "access_token";
  String EMAIL = "email";

  String getClientId();

  String getRedirectUri();

  String getOauthUri();

  String getUser(String code);
}
