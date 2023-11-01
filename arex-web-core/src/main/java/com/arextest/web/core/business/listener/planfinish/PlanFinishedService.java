package com.arextest.web.core.business.listener.planfinish;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * created by xinyuan_wang on 2023/1/12
 */
@Component
public class PlanFinishedService {
    private List<PlanFinishedLinstener> planFinishedLinsteners;

    public PlanFinishedService() {
        ServiceLoader<PlanFinishedLinstener> linsteners = ServiceLoader.load(PlanFinishedLinstener.class);
        planFinishedLinsteners = new ArrayList<>();
        for (PlanFinishedLinstener planFinishedLinstener : linsteners) {
            planFinishedLinsteners.add(planFinishedLinstener);
        }
    }

    public void onPlanFinishEvent(String appId, String planId, Integer status) {
        if (CollectionUtils.isEmpty(this.planFinishedLinsteners)) {
            return;
        }
        for (PlanFinishedLinstener planFinishListener : this.planFinishedLinsteners) {
            planFinishListener.planFinishedAction(appId, planId, status);
        }
    }
}
