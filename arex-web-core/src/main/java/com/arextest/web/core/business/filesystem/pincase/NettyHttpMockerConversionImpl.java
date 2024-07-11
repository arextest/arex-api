package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.model.mock.Mocker;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lhhdz
 * @since 2024/07/11
 */
@Slf4j
public class NettyHttpMockerConversionImpl extends HttpMockerConversionImpl {

    @Override
    public String getCategoryName() {
        return "NettyProvider";
    }


    @Override
    String extractRequestUrl(Mocker mocker) {
        return mocker.getOperationName();
    }
}
