package com.arextest.web.core.business.listener.planfinish;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * created by xinyuan_wang on 2023/1/12
 */
@Component
public class PlanFinishedService {

  @Autowired
  private List<PlanFinishedLinstener> planFinishedLinsteners;

  public void onPlanFinishEvent(String appId, String planId, Integer status) {
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
