package com.arextest.web.core.business.oauth;

import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

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
