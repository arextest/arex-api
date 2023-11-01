package com.arextest.web.api.service.schedule;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.arextest.web.core.business.SceneService;
import com.arextest.web.core.business.StatisticService;
import com.arextest.web.core.business.preprocess.PreprocessService;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Slf4j
@Service
public class StatisticSchedule {
    @Resource
    private StatisticService statisticService;
    @Resource
    private SceneService sceneService;
    @Resource
    private PreprocessService preprocessService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void planItemSchedule() {
        statisticService.report();
    }

    @Scheduled(cron = "1/5 * * * * ?")
    public void sceneSchedule() {
        sceneService.report();
    }

    @Scheduled(cron = "2/60 * * * * ?")
    @SchedulerLock(name = "preprocess", lockAtLeastFor = "PT50S", lockAtMostFor = "PT60S")
    public void preprocess() {
        preprocessService.updateServletSchema();
    }
}
