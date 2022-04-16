package io.arex.report.web.api.service.schedule;

import io.arex.report.core.business.SceneService;
import io.arex.report.core.business.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
public class StatisticSchedule {
    @Resource
    private StatisticService statisticService;
    @Resource
    private SceneService sceneService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void planItemSchedule() {
        statisticService.report();
    }

    @Scheduled(cron = "1/5 * * * * ?")
    public void sceneSchedule() {
        sceneService.report();
    }
}
