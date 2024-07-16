package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.model.mock.Mocker;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lhhdz
 * @since 2024/07/11
 */
@Slf4j
public class ServletHttpMockerConversionImpl extends HttpMockerConversionImpl {
  private static final String SERVLET = "Servlet";

  @Override
  public String getCategoryName() {
    return SERVLET;
  }


  @Override
  String extractRequestUrl(Mocker mocker) {
    return mocker.getTargetRequest().getAttribute(REQUEST_PATH).toString();
  }
}
