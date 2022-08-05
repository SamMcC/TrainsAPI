package com.bjss.trainsapi.scheduling;

import com.bjss.trainsapi.services.ITrainTickService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainTickScheduler {
    private final ITrainTickService trainTickService;

    @Scheduled(fixedRate = 1000)
    public void tickTrains() {
        trainTickService.tickTrainsOnce();
    }
}
