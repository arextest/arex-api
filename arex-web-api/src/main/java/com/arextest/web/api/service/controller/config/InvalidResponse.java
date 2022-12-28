package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;

/**
 * @author jmo
 * @since 2021/12/21
 */
final class InvalidResponse {
    private InvalidResponse() {
    }

    static final Response REQUESTED_APP_ID_IS_EMPTY = ResponseUtils.parameterInvalidResponse("The requested " +
            "appId is empty");
    static final Response REQUESTED_IP_IS_EMPTY = ResponseUtils.parameterInvalidResponse("The requested " +
            "Ip is empty");
    static final Response REQUESTED_INTERFACE_ID_IS_EMPTY = ResponseUtils.parameterInvalidResponse("The requested " +
            "interfaceId is empty");
}
