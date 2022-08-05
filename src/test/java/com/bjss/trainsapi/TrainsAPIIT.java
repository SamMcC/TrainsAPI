package com.bjss.trainsapi;

import com.bjss.trainsapi.controllers.RestExceptionHandler;
import com.bjss.trainsapi.controllers.ScheduleController;
import com.bjss.trainsapi.controllers.StationController;
import com.bjss.trainsapi.controllers.TrainController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class TrainsAPIIT {
    @Autowired
    ScheduleController scheduleController;

    @Autowired
    StationController stationController;

    @Autowired
    TrainController trainController;

    @Autowired
    RestExceptionHandler restExceptionHandler;

    @Test
    public void testContextLoads() {
        assertThat(restExceptionHandler).isNotNull();
        assertThat(scheduleController).isNotNull();
        assertThat(stationController).isNotNull();
        assertThat(trainController).isNotNull();
    }
}
