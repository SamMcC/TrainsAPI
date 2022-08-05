package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.services.IStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@Slf4j
@RequiredArgsConstructor
public class StationController {
    private final IStationService stationService;

    @GetMapping
    public List<Station> findAll() {
        return stationService.findAll();
    }

    @GetMapping("/{id}")
    public Station findById(@PathVariable long id) {
        return stationService.findById(id).orElseThrow(() -> new EntityNotFoundException("Station not found"));
    }

    @GetMapping("/name/{name}")
    public Station findByName(@PathVariable String name) {
        return stationService.findByName(name).orElseThrow(() -> new EntityNotFoundException("Station not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Station createStation(@RequestBody Station station) {
        return stationService.save(station);
    }

    @PutMapping("/{id}")
    public Station updateStation(@RequestBody Station station, @PathVariable long id) {
        Station foundStation = stationService.findById(id).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        return stationService.save(station.withId(foundStation.getId()));
    }

    @DeleteMapping("/{id}")
    public void deleteStation(@PathVariable long id) {
        stationService.deleteById(id);
    }

}
