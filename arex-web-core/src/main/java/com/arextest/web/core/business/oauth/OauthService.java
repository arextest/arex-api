package com.arextest.web.core.business.oauth;

import com.arextest.web.model.contract.contracts.login.VerifyResponseType;

/**
 * @author b_yu
 * @since 2023/8/15
 */
public interface OauthService {
    String getClientId();
    String getUser(String code);
}
