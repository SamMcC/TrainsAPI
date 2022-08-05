package com.bjss.trainsapi.scheduling;

import com.bjss.trainsapi.services.IScheduleGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleGenerationScheduler {
    private final IScheduleGenerationService scheduleGenerationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateSchedules() {
        scheduleGenerationService.generateSchedules();
    }
}
