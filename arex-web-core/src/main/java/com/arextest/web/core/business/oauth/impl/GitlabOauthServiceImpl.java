package com.arextest.web.core.business.oauth.impl;

import com.arextest.web.common.HttpUtils;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.oauth.OauthService;
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
 * @since 2023/8/18
 */
@Slf4j
@Component("GitlabOauth")
public class GitlabOauthServiceImpl implements OauthService {
    @Value("${arex.oauth.gitlab.clientid}")
    private String clientId;
    @Value("${arex.oauth.gitlab.secret}")
    private String secret;
    @Value("${arex.oauth.gitlab.uri}")
    private String gitlabUri;
    @Value("${arex.oauth.gitlab.redirecturi}")
    private String redirectUri;

    private static final String TOKEN_SUFFIX =
            "/oauth/token?client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s";
    private static final String USER_SUFFIX = "/api/v4/user";
    private static final String BEARER = "Bearer ";

    @Override
    public String getClientId() {
        return clientId;
    }
    @Override
    public String getUser(String code) {
        if (StringUtils.isBlank(clientId)) {
            LogUtils.error(LOGGER, "gitlab clientId is null");
            return null;
        }
        if (StringUtils.isBlank(secret)) {
            LogUtils.error(LOGGER, "gitlab secret is null");
            return null;
        }
        String tokenUrl = String.format(gitlabUri + TOKEN_SUFFIX, clientId, secret, code, redirectUri);
        try {
            ResponseEntity<Map> tokenResponse =
                    HttpUtils.post(tokenUrl, new HashMap<>(), Map.class, APPLICATION_JSON, null, TIMEOUT);
            Map<String, String> tokenBody = Objects.requireNonNull(tokenResponse.getBody());
            String accessToken = tokenBody.get(ACCESS_TOKEN);
            ;
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
