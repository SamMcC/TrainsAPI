package com.bjss.trainsapi.services;

import com.bjss.trainsapi.services.impl.TrainService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TrainServiceTest {
    @InjectMocks
    TrainService trainService;
}
