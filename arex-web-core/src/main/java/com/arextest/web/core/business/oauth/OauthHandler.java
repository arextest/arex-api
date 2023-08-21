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

        String userName = oauthService.getUser(code);
        if (StringUtils.isBlank(userName)) {
            response.setSuccess(false);
            response.setReason("oauth login failed");
            return response;
        }

        response.setSuccess(true);
        response.setUserName(userName);
        response.setAccessToken(JwtUtil.makeAccessToken(userName));
        response.setRefreshToken(JwtUtil.makeRefreshToken(userName));

        return response;
    }

    public String getOauthClientId(String oauthType) {
        OauthService oauthService = oauthServiceFactory.getOauthService(oauthType);
        return oauthService.getClientId();
    }
}
