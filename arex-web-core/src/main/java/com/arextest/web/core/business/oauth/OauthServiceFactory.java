package com.arextest.web.core.business.oauth;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author b_yu
 * @since 2023/8/15
 */
@Component
public class OauthServiceFactory {
    @Resource
    private Map<String, OauthService> oauthServiceMap;

    public OauthService getOauthService(String type) {
        return oauthServiceMap.get(type);
    }
}
