package com.arextest.web.core.business.listener.planfinish;

/**
 * created by xinyuan_wang on 2023/1/12
 */
public interface PlanFinishedLinstener {

  String planFinishedAction(String appId, String planId, Integer status);

  String planReCalculateAction(String appId, String planId, Integer status);

}
