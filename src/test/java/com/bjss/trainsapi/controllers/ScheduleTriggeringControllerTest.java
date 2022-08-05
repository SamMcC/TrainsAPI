package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.scheduling.ScheduleGenerationScheduler;
import com.bjss.trainsapi.scheduling.TrainTickScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ScheduleTriggeringControllerTest {
    @Mock
    ScheduleGenerationScheduler scheduleGenerationScheduler;

    @Mock
    TrainTickScheduler trainTickScheduler;

    @InjectMocks
    SchedulerTriggeringController schedulerTriggeringController;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(trainTickScheduler, scheduleGenerationScheduler);
    }

    @Test
    public void testTriggerScheduleGenerator_shouldCallGenerateSchedules() {
        schedulerTriggeringController.triggerScheduleGenerator();
        verify(scheduleGenerationScheduler).generateSchedules();
    }

    @Test
    public void testTriggerTrainTickScheduler_shouldCallGenerateSchedules() {
        schedulerTriggeringController.triggerTrainTickScheduler();
        verify(trainTickScheduler).tickTrains();
    }
}
