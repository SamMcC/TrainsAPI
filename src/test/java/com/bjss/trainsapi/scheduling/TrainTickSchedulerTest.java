package com.bjss.trainsapi.scheduling;

import com.bjss.trainsapi.services.ITrainTickService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TrainTickSchedulerTest {
    @Mock
    ITrainTickService trainTickService;

    @InjectMocks
    TrainTickScheduler trainTickScheduler;

    @Test
    public void testTickTrains_shouldCallTickTrainsOnce() {
        trainTickScheduler.tickTrains();
        verify(trainTickService).tickTrainsOnce();
    }
}
