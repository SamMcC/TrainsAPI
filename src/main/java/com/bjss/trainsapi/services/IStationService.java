package com.bjss.trainsapi.services;

import com.bjss.trainsapi.model.persistence.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface IStationService {
    Optional<Station> findById(long id);

    Optional<Station> findByName(String name);

    Station save(Station station);

    List<Station> findAll();

    void deleteById(long id);

}
