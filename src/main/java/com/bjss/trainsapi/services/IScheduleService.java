package com.bjss.trainsapi.services;

import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Train;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface IScheduleService {

    Optional<Schedule> findById(long id);

    List<Schedule> findByStation(long stationId);

    Optional<Schedule> findNextScheduleForTrain(Train train);

    Schedule save(Schedule schedule);

    List<Schedule> findAll();

    void deleteById(long id);

    void deleteAll();
}
