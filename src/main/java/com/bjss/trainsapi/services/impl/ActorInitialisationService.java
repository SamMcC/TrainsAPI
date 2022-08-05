package com.bjss.trainsapi.services.impl;

import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.model.repository.StationRepository;
import com.bjss.trainsapi.model.repository.TrainRepository;
import com.bjss.trainsapi.services.IActorInitialisationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActorInitialisationService implements IActorInitialisationService {
    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final ObjectMapper objectMapper;

    @Value("${trainsapi.initialisation.actor.trainCount}")
    private int trainCount;

    @Value("${trainsapi.initialisation.actor.stationCount}")
    private int stationCount;

    @Override
    public void initialiseTrains() throws IOException {
        trainRepository.deleteAllInBatch();
        try(InputStream is = this.getClass().getResourceAsStream("/json/train-names.json")) {
            List<String> trainNames = objectMapper.readValue(is, new TypeReference<>() {});
            Collections.shuffle(trainNames);
            List<Train> trains = trainNames
                    .subList(0, trainCount)
                    .stream()
                    .map((name) -> Train.builder().name(name).departureState(Train.DepartureState.READY).build())
                    .collect(Collectors.toList());
            trainRepository.saveAll(trains);
        }
    }

    @Override
    public void initialiseStations() throws IOException {
        stationRepository.deleteAllInBatch();
        try(InputStream is = this.getClass().getResourceAsStream("/json/station-names.json")) {
            List<String> stationNames = objectMapper.readValue(is, new TypeReference<>() {});
            Collections.shuffle(stationNames);
            List<Station> stations = stationNames
                    .subList(0, stationCount)
                    .stream()
                    .map((name) -> Station.builder().name(name).build())
                    .collect(Collectors.toList());
            stationRepository.saveAll(stations);
        }
    }
}
