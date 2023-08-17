package com.arextest.web.core.business.oauth;

import com.arextest.web.common.JwtUtil;
import com.arextest.web.model.contract.contracts.login.VerifyResponseType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author b_yu
 * @since 2023/8/16
 */
@Component
public class OauthHandler {
    @Resource
    private OauthServiceFactory oauthServiceFactory;

    public VerifyResponseType oauthLogin(String code, String oauthType) {
        OauthService oauthService = oauthServiceFactory.getOauthService(oauthType);
        VerifyResponseType response = new VerifyResponseType();

        String githubUser = oauthService.getUser(code);
        if (StringUtils.isBlank(githubUser)) {
            response.setSuccess(false);
            response.setReason("oauth login failed");
            return response;
        }

        response.setSuccess(true);
        response.setAccessToken(JwtUtil.makeAccessToken(githubUser));
        response.setRefreshToken(JwtUtil.makeRefreshToken(githubUser));

        return response;
    }

    public String getOauthClientId(String oauthType) {
        OauthService oauthService = oauthServiceFactory.getOauthService(oauthType);
        return oauthService.getClientId();
    }
}
