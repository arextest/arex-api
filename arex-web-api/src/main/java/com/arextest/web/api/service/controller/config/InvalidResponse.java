package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;

/**
 * @author jmo
 * @since 2021/12/21
 */
public final class InvalidResponse {
    private InvalidResponse() {
    }

    public static final Response REQUESTED_APP_ID_IS_EMPTY = ResponseUtils.parameterInvalidResponse("The requested " +
            "The requested appId is empty");
    static final Response REQUESTED_IP_IS_EMPTY = ResponseUtils.parameterInvalidResponse("The requested " +
            "The requested Ip is empty");
    static final Response REQUESTED_INTERFACE_ID_IS_EMPTY = ResponseUtils.parameterInvalidResponse("The requested " +
            "The requested interfaceId is empty");

}
