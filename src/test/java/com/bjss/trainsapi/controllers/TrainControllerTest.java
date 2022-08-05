package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.services.ITrainService;
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
public class TrainControllerTest {

    @Mock
    private ITrainService trainService;

    @InjectMocks
    private TrainController trainController;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(trainService);
    }

    @Test
    public void testFindAll_shouldCallTrainService() {
        trainController.findAll();
        verify(trainService).findAll();
    }


    @Test
    public void testFindById_shouldReturnTrain_whenExists() {
        when(trainService.findById(eq(1L))).thenReturn(Optional.of(Train.builder().build()));
        Train returnedTrain = trainController.findById(1L);
        verify(trainService).findById(eq(1L));
        assertThat(returnedTrain).isNotNull();
    }

    @Test
    public void testFindById_shouldThrowEntityNotFoundException_whenNotExists() {
        when(trainService.findById(eq(1L))).thenReturn(Optional.empty());
        EntityNotFoundException exception = catchThrowableOfType(() -> trainController.findById(1L), EntityNotFoundException.class);
        verify(trainService).findById(eq(1L));
        assertThat(exception).isNotNull().hasMessage("Train not found");
    }

    @Test
    public void testCreate_shouldSaveTrain() {
        when(trainService.save(any(Train.class))).thenReturn(Train.builder().id(1L).build());
        Train train = trainController.createTrain(Train.builder().id(1L).build());
        verify(trainService).save(any(Train.class));
        assertThat(train).isNotNull().extracting(Train::getId).isEqualTo(1L);
    }

    @Test
    public void testUpdate_shouldSaveTrain_whenTrainExists() {
        when(trainService.findById(eq(1L))).thenReturn(Optional.of(Train.builder().id(1L).build()));
        when(trainService.save(any(Train.class))).thenReturn(Train.builder().id(1L).build());
        Train train = trainController.updateTrain(Train.builder().id(1L).build(), 1L);
        verify(trainService).findById(eq(1L));
        verify(trainService).save(any(Train.class));
        assertThat(train).isNotNull().extracting(Train::getId).isEqualTo(1L);
    }

    @Test
    public void testUpdate_shouldThrowEntityNotFoundException_whenTrainNotExists() {
        when(trainService.findById(eq(1L))).thenReturn(Optional.empty());
        EntityNotFoundException exception = catchThrowableOfType(() ->
                        trainController.updateTrain(Train.builder().id(1L).build(), 1L),
                EntityNotFoundException.class);
        verify(trainService).findById(eq(1L));
        verify(trainService, never()).save(any(Train.class));
        assertThat(exception).isNotNull().hasMessage("Train not found");
    }

    @Test
    public void testUpdate_shouldDeleteTrain() {
        trainController.deleteTrain(1L);
        verify(trainService).deleteById(eq(1L));
    }
}
