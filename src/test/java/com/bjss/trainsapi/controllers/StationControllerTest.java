package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.services.IStationService;
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
public class StationControllerTest {

    @Mock
    private IStationService stationService;

    @InjectMocks
    private StationController stationController;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(stationService);
    }

    @Test
    public void testFindAll_shouldCallStationService() {
        stationController.findAll();
        verify(stationService).findAll();
    }


    @Test
    public void testFindById_shouldReturnStation_whenExists() {
        when(stationService.findById(eq(1L))).thenReturn(Optional.of(Station.builder().build()));
        Station returnedStation = stationController.findById(1L);
        verify(stationService).findById(eq(1L));
        assertThat(returnedStation).isNotNull();
    }

    @Test
    public void testFindById_shouldThrowEntityNotFoundException_whenNotExists() {
        when(stationService.findById(eq(1L))).thenReturn(Optional.empty());
        EntityNotFoundException exception = catchThrowableOfType(() -> stationController.findById(1L), EntityNotFoundException.class);
        verify(stationService).findById(eq(1L));
        assertThat(exception).isNotNull().hasMessage("Station not found");
    }

    @Test
    public void testCreate_shouldSaveStation() {
        when(stationService.save(any(Station.class))).thenReturn(Station.builder().id(1L).build());
        Station station = stationController.createStation(Station.builder().id(1L).build());
        verify(stationService).save(any(Station.class));
        assertThat(station).isNotNull().extracting(Station::getId).isEqualTo(1L);
    }

    @Test
    public void testUpdate_shouldSaveStation_whenStationExists() {
        when(stationService.findById(eq(1L))).thenReturn(Optional.of(Station.builder().id(1L).build()));
        when(stationService.save(any(Station.class))).thenReturn(Station.builder().id(1L).build());
        Station station = stationController.updateStation(Station.builder().id(1L).build(), 1L);
        verify(stationService).findById(eq(1L));
        verify(stationService).save(any(Station.class));
        assertThat(station).isNotNull().extracting(Station::getId).isEqualTo(1L);
    }

    @Test
    public void testUpdate_shouldThrowEntityNotFoundException_whenStationNotExists() {
        when(stationService.findById(eq(1L))).thenReturn(Optional.empty());
        EntityNotFoundException exception = catchThrowableOfType(() ->
                        stationController.updateStation(Station.builder().id(1L).build(), 1L),
                EntityNotFoundException.class);
        verify(stationService).findById(eq(1L));
        verify(stationService, never()).save(any(Station.class));
        assertThat(exception).isNotNull().hasMessage("Station not found");
    }

    @Test
    public void testUpdate_shouldDeleteStation() {
        stationController.deleteStation(1L);
        verify(stationService).deleteById(eq(1L));
    }
}
