package com.bjss.trainsapi.services.impl;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.model.repository.ScheduleRepository;
import com.bjss.trainsapi.model.repository.StationRepository;
import com.bjss.trainsapi.services.IScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService implements IScheduleService {
    private final ScheduleRepository scheduleRepository;

    private final StationRepository stationRepository;

    @Override
    public Optional<Schedule> findById(long id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public Optional<Schedule> findNextScheduleForTrain(Train train) {
        Schedule currentTrainSchedule = train.getCurrentSchedule();
        LocalDateTime currentScheduledDepartureTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        if (currentTrainSchedule != null && currentTrainSchedule.getScheduledDepartureTime() != null) {
            currentScheduledDepartureTime = currentTrainSchedule.getScheduledDepartureTime();
        }
        return scheduleRepository.findFirstByTrainAndScheduledDepartureTimeAfterOrderByScheduledDepartureTimeAsc(
            train, currentScheduledDepartureTime
        );
    }

    @Override
    public List<Schedule> findByStation(long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        return scheduleRepository.findByStation(station);
    }

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public void deleteById(long id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        scheduleRepository.deleteAll();
    }
}
