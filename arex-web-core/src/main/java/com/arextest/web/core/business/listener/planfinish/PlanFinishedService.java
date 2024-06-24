package com.arextest.web.core.business.listener.planfinish;

import com.arextest.web.common.LogUtils;
import com.arextest.web.common.LogUtils.LogTagKeySummary;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * created by xinyuan_wang on 2023/1/12
 */
@Slf4j
@Component
public class PlanFinishedService {

  @Autowired
  private List<PlanFinishedLinstener> planFinishedLinsteners;

  public void onPlanFinishEvent(String appId, String planId, Integer status) {
    LogUtils.info(LOGGER,
        ImmutableMap.of(
            LogTagKeySummary.PLAN_ID, planId,
            LogTagKeySummary.PLAN_STATUS, String.valueOf(status)
        ),
        "Plan finished event, appId: {}, planId: {}, status: {}", appId, planId, status);

    if (CollectionUtils.isEmpty(this.planFinishedLinsteners)) {
      return;
    }
    for (PlanFinishedLinstener planFinishListener : this.planFinishedLinsteners) {
      planFinishListener.planFinishedAction(appId, planId, status);
    }
  }

  public void onPlanReCalculateEvent(String appId, String planId, Integer status) {
    if (CollectionUtils.isEmpty(this.planFinishedLinsteners)) {
      return;
    }
    for (PlanFinishedLinstener planFinishListener : this.planFinishedLinsteners) {
      planFinishListener.planReCalculateAction(appId, planId, status);
    }
  }
}
