package com.bjss.trainsapi.services;

import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.services.impl.ScheduleGenerationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleGenerationServiceTest {
    @Mock
    IScheduleService scheduleService;

    @Mock
    IStationService stationService;

    @Mock
    ITrainService trainService;

    @InjectMocks
    ScheduleGenerationService scheduleGenerationService;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(scheduleGenerationService, "scheduleDowntime", Duration.ofMinutes(2));
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(scheduleService, trainService, stationService);
    }

    @Test
    public void testGenerateSchedules_shouldCreateUniqueSchedulesForTrain() {
        ArgumentCaptor<Schedule> scheduleArgumentCaptor = ArgumentCaptor.forClass(Schedule.class);
        Train train = Train.builder().build();
        when(trainService.findAll()).thenReturn(List.of(train));
        when(stationService.findAll()).thenReturn(List.of(Station.builder().id(1L).name("Test1").build(),Station.builder().id(2L).name("Test2").build()));
        when(scheduleService.findNextScheduleForTrain(eq(train))).thenReturn(Optional.of(Schedule.builder().build()));
        when(trainService.save(any(Train.class))).thenReturn(train.withCurrentSchedule(Schedule.builder().build()));
        scheduleGenerationService.generateSchedules();
        verify(trainService).findAll();
        verify(stationService).findAll();
        verify(scheduleService, atLeastOnce()).save(scheduleArgumentCaptor.capture());
        verify(scheduleService).findNextScheduleForTrain(any(Train.class));
        verify(trainService).save(any(Train.class));
        assertThat(scheduleArgumentCaptor.getAllValues()).satisfies(schedules -> {
            assertThat(schedules).allSatisfy(schedule -> {
                assertThat(schedule.getSource()).isNotNull();
                assertThat(schedule.getDestination()).isNotNull().isNotSameAs(schedule.getSource());
                assertThat(schedule.getScheduledArrivalTime()).isAfter(schedule.getScheduledDepartureTime());
                assertThat(schedule.getTrain()).isEqualTo(train);
            });
            assertThat(schedules).extracting(Schedule::getScheduledDepartureTime).doesNotHaveDuplicates();
            assertThat(schedules).extracting(Schedule::getScheduledArrivalTime).doesNotHaveDuplicates();
        });
    }

    @Test
    public void testGenerateSchedules_shouldCreateSchedulesForAllTrains() {
        ArgumentCaptor<Schedule> scheduleArgumentCaptor = ArgumentCaptor.forClass(Schedule.class);
        Train train1 = Train.builder().id(1L).build();
        Train train2 = Train.builder().id(2L).build();
        Station station1 = Station.builder().name("Test1").build();
        Station station2 = Station.builder().name("Test2").build();
        when(trainService.findAll()).thenReturn(List.of(train1, train2));
        when(stationService.findAll()).thenReturn(List.of(station1, station2));
        Schedule schedule1 = Schedule.builder().scheduledDepartureTime(LocalDateTime.now()).scheduledArrivalTime(LocalDateTime.now().plus(Duration.ofMinutes(10))).source(station1).destination(station2).build();
        Schedule schedule2 = Schedule.builder().scheduledDepartureTime(LocalDateTime.now()).scheduledArrivalTime(LocalDateTime.now().plus(Duration.ofMinutes(10))).source(station2).destination(station1).build();
        when(scheduleService.findNextScheduleForTrain(eq(train1))).thenReturn(Optional.of(schedule1));
        when(scheduleService.findNextScheduleForTrain(eq(train2))).thenReturn(Optional.of(schedule2));
        when(trainService.save(eq(train1))).thenReturn(train1.withCurrentSchedule(schedule1));
        when(trainService.save(eq(train2))).thenReturn(train2.withCurrentSchedule(schedule2));
        scheduleGenerationService.generateSchedules();
        verify(trainService).findAll();
        verify(stationService).findAll();
        verify(scheduleService, atLeastOnce()).save(scheduleArgumentCaptor.capture());
        verify(scheduleService, times(2)).findNextScheduleForTrain(any(Train.class));
        verify(trainService, times(2)).save(any(Train.class));
        assertThat(scheduleArgumentCaptor.getAllValues()).allSatisfy(schedule -> {
            assertThat(schedule.getSource()).isNotNull();
            assertThat(schedule.getDestination()).isNotNull().isNotSameAs(schedule.getSource());
            assertThat(schedule.getScheduledArrivalTime()).isAfter(schedule.getScheduledDepartureTime());
            assertThat(schedule.getTrain()).isIn(train1, train2);
        });
    }

    @Test
    public void testGenerateSchedules_shouldThrowException_whenNotEnoughStations() {
        Station station = Station.builder().build();
        when(stationService.findAll()).thenReturn(List.of(station));
        RuntimeException exception = catchThrowableOfType(() -> scheduleGenerationService.generateSchedules(), RuntimeException.class);
        verify(stationService).findAll();
        verify(trainService).findAll();
        assertThat(exception).isNotNull().hasMessage("No stations available for schedule generation");
    }

    @Test
    public void testGenerateSchedules_shouldThrowException_whenNotEnoughTrains() {
        Station station1 = Station.builder().build();
        Station station2 = Station.builder().build();
        when(stationService.findAll()).thenReturn(List.of(station1, station2));
        when(trainService.findAll()).thenReturn(Collections.emptyList());
        RuntimeException exception = catchThrowableOfType(() -> scheduleGenerationService.generateSchedules(), RuntimeException.class);
        verify(stationService).findAll();
        verify(trainService).findAll();
        assertThat(exception).isNotNull().hasMessage("No trains available for schedule generation");
    }

}
