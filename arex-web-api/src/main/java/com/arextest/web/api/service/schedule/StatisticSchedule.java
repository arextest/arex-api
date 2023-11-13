package com.arextest.web.api.service.schedule;

import com.arextest.web.core.business.SceneService;
import com.arextest.web.core.business.StatisticService;
import com.arextest.web.core.business.preprocess.PreprocessService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatisticSchedule {

  @Lazy
  @Resource
  private StatisticService statisticService;

  @Lazy
  @Resource
  private SceneService sceneService;

  @Lazy
  @Resource
  private PreprocessService preprocessService;


  @Scheduled(initialDelay = 1000 * 5, fixedDelay = 1000 * 5)
  public void planItemSchedule() {
    statisticService.report();
  }

  @Scheduled(initialDelay = 1000 * 6, fixedDelay = 1000 * 5)
  public void sceneSchedule() {
    sceneService.report();
  }

  @Scheduled(initialDelay = 1000 * 7, fixedDelay = 1000 * 60)
  @SchedulerLock(name = "preprocess", lockAtLeastFor = "PT50S", lockAtMostFor = "PT60S")
  public void preprocess() {
    preprocessService.updateServletSchema();
  }
}
