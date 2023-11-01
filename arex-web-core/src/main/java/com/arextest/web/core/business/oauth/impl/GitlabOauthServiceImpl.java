package com.arextest.web.core.business.oauth.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.arextest.web.common.HttpUtils;
import com.arextest.web.common.LogUtils;

import lombok.extern.slf4j.Slf4j;

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
    @Value("${arex.oauth.gitlab.redirecturi}")
    private String redirectUri;

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getRedirectUri() {
        return redirectUri;
    }

    @Override
    public String getOauthUri() {
        return gitlabUri;
    }

    @Override
    public String getUser(String code) {
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
            ResponseEntity<Map> tokenResponse =
                HttpUtils.post(tokenUrl, new HashMap<>(), Map.class, APPLICATION_JSON, null, TIMEOUT);
            Map<String, String> tokenBody = Objects.requireNonNull(tokenResponse.getBody());
            String accessToken = tokenBody.get(ACCESS_TOKEN);;
            Map<String, String> headers = new HashMap<>();
            headers.put(AUTHORIZATION, BEARER + accessToken);
            ResponseEntity<Map> userResponse =
                HttpUtils.get(gitlabUri + USER_SUFFIX, Map.class, APPLICATION_JSON, headers, TIMEOUT);
            Map<String, String> userBody = Objects.requireNonNull(userResponse.getBody());
            return userBody.get(EMAIL);
        } catch (Exception e) {
            LogUtils.error(LOGGER, "gitlab get user error", e);
            return null;
        }
    }
}
