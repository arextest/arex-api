package com.arextest.web.core.business.oauth.impl;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.oauth.OauthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author b_yu
 * @since 2023/8/22
 */
@Slf4j
public abstract class AbstractOauthServiceImpl implements OauthService {
    protected boolean checkOauth(String clientId, String secret, String code) {
        if (StringUtils.isBlank(code)) {
            LogUtils.error(LOGGER, "Oauth code is null");
            return false;
        }
        if (StringUtils.isBlank(clientId)) {
            LogUtils.error(LOGGER, "Oauth clientId is null");
            return false;
        }
        if (StringUtils.isBlank(secret)) {
            LogUtils.error(LOGGER, "Oauth secret is null");
            return false;
        }
        return true;
    }
}
