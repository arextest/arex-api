package com.arextest.web.core.business;

import com.arextest.web.core.business.beans.httpclient.HttpWebServiceApiClient;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduleService {

  @Resource
  private HttpWebServiceApiClient httpWebServiceApiClient;

  @Value("${arex.schedule.stop.url}")
  private String stopPlanUrl;

  public String stopPlan(String planId) {
    if (StringUtils.isEmpty(planId)) {
      return null;
    }

    Map<String, String> stringStringMap = Collections.singletonMap("planId", planId);
    String res = httpWebServiceApiClient.getWithInterceptors(stopPlanUrl, stringStringMap,
        String.class);
    if (StringUtils.isEmpty(res)) {
      return null;
    }
    return res;
  }

}
