package com.bjss.trainsapi.services;

import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.services.impl.TrainTickService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.bjss.trainsapi.model.persistence.Train.DepartureState.ARRIVED;
import static com.bjss.trainsapi.model.persistence.Train.DepartureState.DEPARTED;
import static com.bjss.trainsapi.model.persistence.Train.DepartureState.READY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainTickServiceTest {
    public static final Station STATION_1 = Station.builder().id(1L).name("Test1").build();
    public static final Station STATION_2 = Station.builder().id(2L).name("Test2").build();
    @Mock
    IScheduleService scheduleService;

    @Mock
    ITrainService trainService;

    @Mock
    IStationService stationService;

    @InjectMocks
    TrainTickService trainTickService;

    @Test
    public void testTickTrainsOnce_shouldMoveDepartedTrainsForward_whenDistanceToDestinationIsNonZero(){
        Schedule schedule = Schedule.builder().destination(STATION_2).source(STATION_1).scheduledDepartureTime(LocalDateTime.MIN).scheduledArrivalTime(LocalDateTime.MAX).build();
        Train train = Train.builder().currentSchedule(schedule).speed(100).departureState(DEPARTED).distanceToDestination(Integer.MAX_VALUE).build();
        when(trainService.findAll()).thenReturn(List.of(train));

        trainTickService.tickTrainsOnce();
        ArgumentCaptor<Train> trainArgumentCaptor = ArgumentCaptor.forClass(Train.class);
        verify(trainService).save(trainArgumentCaptor.capture());
        assertThat(trainArgumentCaptor.getValue()).isNotNull().satisfies(savedTrain -> {
            assertThat(savedTrain.getDistanceToDestination()).isLessThan(train.getDistanceToDestination());
            assertThat(savedTrain.getId()).isEqualTo(train.getId());
            assertThat(savedTrain.getDepartureState()).isEqualTo(DEPARTED);
        });
    }

    @Test
    public void testTickTrainsOnce_shouldSetTrainsToArrived_andUpdateActualArrivalTime_whenDistanceToDestinationIsZero(){
        Schedule schedule = Schedule.builder().destination(STATION_2).source(STATION_1).scheduledDepartureTime(LocalDateTime.MIN).scheduledArrivalTime(LocalDateTime.MAX).build();
        Train train = Train.builder().currentSchedule(schedule).speed(100).departureState(DEPARTED).distanceToDestination(1).build();
        when(trainService.findAll()).thenReturn(List.of(train));

        trainTickService.tickTrainsOnce();
        ArgumentCaptor<Train> trainArgumentCaptor = ArgumentCaptor.forClass(Train.class);
        verify(trainService).save(trainArgumentCaptor.capture());
        assertThat(trainArgumentCaptor.getValue()).isNotNull().satisfies(savedTrain -> {
            assertThat(savedTrain.getDistanceToDestination()).isEqualTo(0);
            assertThat(savedTrain.getId()).isEqualTo(train.getId());
            assertThat(savedTrain.getDepartureState()).isEqualTo(ARRIVED);
        });

        ArgumentCaptor<Schedule> scheduleArgumentCaptor = ArgumentCaptor.forClass(Schedule.class);
        verify(scheduleService).save(scheduleArgumentCaptor.capture());

        assertThat(scheduleArgumentCaptor.getValue()).isNotNull().satisfies(savedSchedule -> {
            assertThat(savedSchedule.getActualDepartureTime()).isNull();
            assertThat(savedSchedule.getActualArrivalTime()).isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.MINUTES));
        });
    }

    @Test
    public void testTickTrainsOnce_shouldSetTrainsToDeparted_whenDepartureTimeIsNowOrEarlier(){
        Schedule schedule = Schedule.builder().destination(STATION_2).source(STATION_1).scheduledDepartureTime(LocalDateTime.now().minusMinutes(1)).scheduledArrivalTime(LocalDateTime.MAX).build();
        Train train = Train.builder().currentSchedule(schedule).speed(100).departureState(READY).distanceToDestination(Integer.MAX_VALUE).build();
        when(trainService.findAll()).thenReturn(List.of(train));

        trainTickService.tickTrainsOnce();

        ArgumentCaptor<Train> trainArgumentCaptor = ArgumentCaptor.forClass(Train.class);
        verify(trainService).save(trainArgumentCaptor.capture());
        assertThat(trainArgumentCaptor.getValue()).isNotNull().satisfies(savedTrain -> {
            assertThat(savedTrain.getDistanceToDestination()).isLessThan(train.getDistanceToDestination());
            assertThat(savedTrain.getId()).isEqualTo(train.getId());
            assertThat(savedTrain.getDepartureState()).isEqualTo(DEPARTED);
        });

        ArgumentCaptor<Schedule> scheduleArgumentCaptor = ArgumentCaptor.forClass(Schedule.class);
        verify(scheduleService).save(scheduleArgumentCaptor.capture());

        assertThat(scheduleArgumentCaptor.getValue()).isNotNull().satisfies(savedSchedule -> {
            assertThat(savedSchedule.getActualDepartureTime()).isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.MINUTES));
            assertThat(savedSchedule.getActualArrivalTime()).isNull();
        });
    }

    @Test
    public void testTickTrainsOnce_shouldGetNextScheduleForArrivedTrains_andSetStateToReady_andUpdateDistanceToDestination(){
        Schedule schedule1 = Schedule.builder().destination(STATION_2).source(STATION_1).scheduledDepartureTime(LocalDateTime.now()).scheduledArrivalTime(LocalDateTime.MAX).build();
        Schedule schedule2 = Schedule.builder().destination(STATION_1).source(STATION_2).scheduledDepartureTime(LocalDateTime.now().plusMinutes(2)).scheduledArrivalTime(LocalDateTime.MAX).build();
        Train train = Train.builder().currentSchedule(schedule1).speed(100).departureState(ARRIVED).distanceToDestination(0).build();
        when(trainService.findAll()).thenReturn(List.of(train));
        when(scheduleService.findNextScheduleForTrain(eq(train))).thenReturn(Optional.of(schedule2));

        trainTickService.tickTrainsOnce();

        ArgumentCaptor<Train> trainArgumentCaptor = ArgumentCaptor.forClass(Train.class);
        verify(trainService).save(trainArgumentCaptor.capture());
        assertThat(trainArgumentCaptor.getValue()).isNotNull().satisfies(savedTrain -> {
            assertThat(savedTrain.getDistanceToDestination()).isEqualTo(STATION_1.getCoordinates().distanceToCoordinate(STATION_2.getCoordinates()));
            assertThat(savedTrain.getId()).isEqualTo(train.getId());
            assertThat(savedTrain.getDepartureState()).isEqualTo(READY);
        });
    }
}
