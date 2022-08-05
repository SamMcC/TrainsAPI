package com.bjss.trainsapi.scheduling;

import com.bjss.trainsapi.services.IScheduleGenerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScheduleGenerationSchedulerTest {
    @Mock
    IScheduleGenerationService scheduleGenerationService;

    @InjectMocks
    ScheduleGenerationScheduler scheduleGenerationScheduler;

    @Test
    public void testGenerateSchedules_shouldCallGenerateSchedules() {
        scheduleGenerationScheduler.generateSchedules();
        verify(scheduleGenerationService).generateSchedules();
    }
}
