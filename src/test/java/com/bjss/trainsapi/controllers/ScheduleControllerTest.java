package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.services.IScheduleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest {

    @Mock
    private IScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(scheduleService);
    }

    @Test
    public void testFindAll_shouldCallScheduleService() {
        scheduleController.findAll();
        verify(scheduleService).findAll();
    }


    @Test
    public void testFindById_shouldReturnSchedule_whenExists() {
        when(scheduleService.findById(eq(1L))).thenReturn(Optional.of(Schedule.builder().build()));
        Schedule returnedSchedule = scheduleController.findById(1L);
        verify(scheduleService).findById(eq(1L));
        assertThat(returnedSchedule).isNotNull();
    }

    @Test
    public void testFindById_shouldThrowEntityNotFoundException_whenNotExists() {
        when(scheduleService.findById(eq(1L))).thenReturn(Optional.empty());
        EntityNotFoundException exception = catchThrowableOfType(() -> scheduleController.findById(1L), EntityNotFoundException.class);
        verify(scheduleService).findById(eq(1L));
        assertThat(exception).isNotNull().hasMessage("Schedule not found");
    }

    @Test
    public void testCreate_shouldSaveSchedule() {
        when(scheduleService.save(any(Schedule.class))).thenReturn(Schedule.builder().id(1L).build());
        Schedule schedule = scheduleController.createSchedule(Schedule.builder().id(1L).build());
        verify(scheduleService).save(any(Schedule.class));
        assertThat(schedule).isNotNull().extracting(Schedule::getId).isEqualTo(1L);
    }

    @Test
    public void testUpdate_shouldSaveSchedule_whenScheduleExists() {
        when(scheduleService.findById(eq(1L))).thenReturn(Optional.of(Schedule.builder().id(1L).build()));
        when(scheduleService.save(any(Schedule.class))).thenReturn(Schedule.builder().id(1L).build());
        Schedule schedule = scheduleController.updateSchedule(Schedule.builder().id(1L).build(), 1L);
        verify(scheduleService).findById(eq(1L));
        verify(scheduleService).save(any(Schedule.class));
        assertThat(schedule).isNotNull().extracting(Schedule::getId).isEqualTo(1L);
    }

    @Test
    public void testUpdate_shouldThrowEntityNotFoundException_whenScheduleNotExists() {
        when(scheduleService.findById(eq(1L))).thenReturn(Optional.empty());
        EntityNotFoundException exception = catchThrowableOfType(() ->
                scheduleController.updateSchedule(Schedule.builder().id(1L).build(), 1L),
                EntityNotFoundException.class);
        verify(scheduleService).findById(eq(1L));
        verify(scheduleService, never()).save(any(Schedule.class));
        assertThat(exception).isNotNull().hasMessage("Schedule not found");
    }

    @Test
    public void testUpdate_shouldDeleteSchedule() {
        scheduleController.deleteSchedule(1L);
        verify(scheduleService).deleteById(eq(1L));
    }
}
