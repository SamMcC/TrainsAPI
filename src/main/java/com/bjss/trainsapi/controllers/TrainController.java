package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.services.ITrainService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/trains")
@RequiredArgsConstructor
public class TrainController {
    private final ITrainService trainService;

    @GetMapping
    public List<Train> findAll() {
        return trainService.findAll();
    }

    @GetMapping("/{id}")
    public Train findById(@PathVariable long id) {
        return trainService.findById(id).orElseThrow(() -> new EntityNotFoundException("Train not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Train createTrain(@RequestBody Train train) {
        return trainService.save(train);
    }

    @PutMapping("/{id}")
    public Train updateTrain(@RequestBody Train train, @PathVariable long id) {
        Train foundTrain = trainService.findById(id).orElseThrow(() -> new EntityNotFoundException("Train not found"));
        return trainService.save(train.withId(foundTrain.getId()));
    }

    @DeleteMapping("/{id}")
    public void deleteTrain(@PathVariable long id) {
        trainService.deleteById(id);
    }
}
