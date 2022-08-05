package com.bjss.trainsapi.services;

import com.bjss.trainsapi.model.persistence.Train;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ITrainService {

    Optional<Train> findById(long id);

    Optional<Train> findBySchedule(long scheduleId);

    List<Train> findByDestinationStation(long stationId);

    List<Train> findByDepartureStation(long stationId);

    Train save(Train train);

    List<Train> saveAll(List<Train> trains);

    List<Train> findAll();

    void deleteById(long id);


}
