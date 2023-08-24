package com.arextest.web.core.business.oauth;

import com.arextest.web.model.contract.contracts.login.VerifyResponseType;

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
    String getUser(String code);
}
