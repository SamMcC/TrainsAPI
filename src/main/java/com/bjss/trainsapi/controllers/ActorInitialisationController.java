package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.scheduling.ScheduleGenerationScheduler;
import com.bjss.trainsapi.services.IActorInitialisationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/init")
@Slf4j
public class ActorInitialisationController {
    private final IActorInitialisationService actorInitialisationService;
    private final ScheduleGenerationScheduler scheduleGenerationScheduler;

    @PostMapping("init-all")
    public void initAll() throws IOException {
        initTrains();
        initStations();
        initSchedules();
    }

    @PostMapping("init-trains")
    public void initTrains() throws IOException {
        actorInitialisationService.initialiseTrains();
    }

    @PostMapping("init-stations")
    public void initStations() throws IOException {
        actorInitialisationService.initialiseStations();
    }

    @PostMapping("init-schedules")
    public void initSchedules() {
        scheduleGenerationScheduler.generateSchedules();
    }
}
