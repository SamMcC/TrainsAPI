package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.scheduling.ScheduleGenerationScheduler;
import com.bjss.trainsapi.scheduling.TrainTickScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/scheduling")
@RequiredArgsConstructor
public class SchedulerTriggeringController {
    private final ScheduleGenerationScheduler scheduleGenerationScheduler;
    private final TrainTickScheduler trainTickScheduler;

    @PostMapping("generate-schedules")
    public void triggerScheduleGenerator() {
        scheduleGenerationScheduler.generateSchedules();
    }

    @PostMapping("trigger-train-tick")
    public void triggerTrainTickScheduler() {
        trainTickScheduler.tickTrains();
    }
}
