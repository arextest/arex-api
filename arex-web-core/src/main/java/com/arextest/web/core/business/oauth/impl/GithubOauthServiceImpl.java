package com.arextest.web.core.business.oauth.impl;

import com.arextest.web.common.HttpUtils;
import com.arextest.web.common.JwtUtil;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.oauth.OauthService;
import com.arextest.web.model.contract.contracts.login.VerifyResponseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author b_yu
 * @since 2023/8/15
 */
@Slf4j
@Component("GithubOauth")
public class GithubOauthServiceImpl implements OauthService {
    private static final String GITHUB_ACCESS_TOKEN_URL =
            "https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s";
    private static final String GITHUB_USER_URL = "https://api.github.com/user";
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "token ";
    private static final String ACCEPT = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EMAIL = "email";
    @Value("${arex.oauth.github.clientId}")
    private String clientId;
    @Value("${arex.oauth.github.secret}")
    private String secret;
    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getUser(String code) {
        if (StringUtils.isBlank(clientId)){
            LogUtils.error(LOGGER, "github clientId is null");
            return null;
        }
        if (StringUtils.isBlank(secret)){
            LogUtils.error(LOGGER, "github secret is null");
            return null;
        }
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put(ACCEPT, APPLICATION_JSON);
            ResponseEntity<Map> tokenResponse =
                    HttpUtils.get(String.format(GITHUB_ACCESS_TOKEN_URL, clientId, secret, code),
                            Map.class,
                            APPLICATION_JSON,
                            headers,
                            2000);
            Map<String, String> tokenBody = Objects.requireNonNull(tokenResponse.getBody());
            String accessToken = tokenBody.get(ACCESS_TOKEN);

            headers = new HashMap<>();
            headers.put(AUTHORIZATION, TOKEN + accessToken);
            headers.put(ACCEPT, APPLICATION_JSON);
            ResponseEntity<Map> result = HttpUtils.get(GITHUB_USER_URL, Map.class, APPLICATION_JSON, headers, 2000);
            Map<String, String> body = Objects.requireNonNull(result.getBody());
            return body.get(EMAIL);
        } catch (Exception e) {
            LogUtils.error(LOGGER, "get github user error", e);
            return null;
        }
    }
}
