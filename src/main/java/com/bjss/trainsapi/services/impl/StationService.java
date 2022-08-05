package com.bjss.trainsapi.services.impl;

import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.repository.StationRepository;
import com.bjss.trainsapi.services.IStationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationService implements IStationService {
    final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public Optional<Station> findById(long id) {
        return stationRepository.findById(id);
    }

    @Override
    public Optional<Station> findByName(String name) {
        return stationRepository.findByName(name);
    }

    @Override
    public Station save(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    @Override
    public void deleteById(long id) {
        stationRepository.deleteById(id);
    }
}
