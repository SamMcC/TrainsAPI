package com.bjss.trainsapi.services.impl;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.model.repository.ScheduleRepository;
import com.bjss.trainsapi.model.repository.StationRepository;
import com.bjss.trainsapi.model.repository.TrainRepository;
import com.bjss.trainsapi.services.ITrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainService implements ITrainService {
    private final TrainRepository trainRepository;

    private final StationRepository stationRepository;

    private final ScheduleRepository scheduleRepository;

    @Override
    public Optional<Train> findById(long id) {
        return trainRepository.findById(id);
    }

    @Override
    public Optional<Train> findBySchedule(long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        return trainRepository.findBySchedulesContaining(schedule);
    }

    @Override
    public List<Train> findByDestinationStation(long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        List<Schedule> schedules = scheduleRepository.findByStation(station);
        return schedules.stream()
                .filter(schedule -> schedule.getDestination().equals(station))
                .map(Schedule::getTrain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Train> findByDepartureStation(long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        List<Schedule> schedules = scheduleRepository.findByStation(station);
        return schedules.stream()
                .filter(schedule -> schedule.getSource().equals(station))
                .map(Schedule::getTrain)
                .collect(Collectors.toList());
    }

    @Override
    public Train save(Train train) {
        return trainRepository.save(train);
    }

    @Override
    public List<Train> saveAll(List<Train> trains) {
        return trainRepository.saveAll(trains);
    }

    @Override
    public List<Train> findAll() {
        return trainRepository.findAll();
    }

    @Override
    public void deleteById(long id) {
        trainRepository.deleteById(id);
    }
}
