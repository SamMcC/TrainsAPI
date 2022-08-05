package com.bjss.trainsapi.services.impl;

import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.services.IScheduleGenerationService;
import com.bjss.trainsapi.services.IScheduleService;
import com.bjss.trainsapi.services.IStationService;
import com.bjss.trainsapi.services.ITrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
//  todo: make trains run routes with pre-set stations
//  todo: reduce service during off-peak hours, add special stations
public class ScheduleGenerationService implements IScheduleGenerationService {

    private final ITrainService trainService;
    private final IStationService stationService;
    private final IScheduleService scheduleService;

    private static final LocalTime TRAIN_SERVICE_START_TIME = LocalTime.of(8, 0);
    private static final LocalTime TRAIN_SERVICE_STOP_TIME = LocalTime.of(22, 0);

    @Value("${trainsapi.schedules.downtime:PT2m}")
    private Duration scheduleDowntime;

    private static final Function<Double, Duration> calculateTime = (distance) -> Duration.ofMinutes((long) (distance / 40));

    @Override
    public void generateSchedules() {
        List<Train> trains = trainService.findAll();
        List<Station> stations = stationService.findAll();

        if (stations.size() < 2) {
            throw new RuntimeException("No stations available for schedule generation");
        }

        if (trains.isEmpty()) {
            throw new RuntimeException("No trains available for schedule generation");
        }

        generateSchedulesForTrainsAndStations(trains, stations);
    }

    private void generateSchedulesForTrainsAndStations(List<Train> trains, List<Station> stations) {
        LocalDate today = LocalDate.now();
        LocalDateTime trainServiceStopTime = LocalDateTime.of(today, TRAIN_SERVICE_STOP_TIME);
        LocalDateTime trainServiceStartTime = LocalDateTime.of(today, TRAIN_SERVICE_START_TIME);
        log.info("Generating schedules for [{}] stations on [{}] trains between [{}] and [{}]", stations.size(), trains.size(), trainServiceStartTime, trainServiceStopTime);

        for (Train train : trains) {
            generateAllSchedulesForTrain(stations, trainServiceStopTime, train);
            Schedule firstScheduleForTrain = scheduleService.findNextScheduleForTrain(train).orElseThrow(EntityNotFoundException::new);
            Train savedTrain = trainService.save(train.withDepartureState(Train.DepartureState.READY)
                    .withCurrentSchedule(firstScheduleForTrain)
                    .withCurrentLocation(firstScheduleForTrain.getSource()));
            log.info("Updated train [{}], new designation is [{}]", savedTrain.getId(), savedTrain.getCurrentTrainDesignation());
        }
    }

    private void generateAllSchedulesForTrain(List<Station> stations, LocalDateTime trainServiceStopTime, Train train) {
        LocalDate today = LocalDate.now();
        Schedule previousSchedule = Schedule.builder().scheduledArrivalTime(LocalDateTime.MIN).build();
        LocalDateTime estimatedDepartureTime = LocalDateTime.of(today, TRAIN_SERVICE_START_TIME);
        Station source = train.getCurrentLocation();
        if (source == null) {
            source = stations.get(RandomUtils.nextInt() % stations.size());
        }

        while (previousSchedule.getScheduledArrivalTime()
                .plus(scheduleDowntime)
                .isBefore(trainServiceStopTime)
        ) {
            int destinationStationIndex = getNextDestinationStationIndex(stations.indexOf(source), stations.size());

            Station destination = stations.get(destinationStationIndex);
            double distanceBetweenStations = source.getCoordinates().distanceToCoordinate(destination.getCoordinates());
            Duration estimatedJourneyTime = calculateTime.apply(distanceBetweenStations);
            LocalDateTime estimatedArrivalTime = estimatedDepartureTime.plus(estimatedJourneyTime);
            Schedule currentSchedule = Schedule.builder().scheduledDepartureTime(estimatedDepartureTime).scheduledArrivalTime(estimatedArrivalTime).train(train).destination(destination).source(source).build();
            scheduleService.save(currentSchedule);
            estimatedDepartureTime = estimatedArrivalTime.plus(scheduleDowntime);
            source = stations.get(destinationStationIndex);
            previousSchedule = currentSchedule;
        }
    }

    private int getNextDestinationStationIndex(int sourceStationIndex, int numberOfStations) {
        int destinationStationIndex = RandomUtils.nextInt() % numberOfStations;

        if (sourceStationIndex == destinationStationIndex) {
            destinationStationIndex = (destinationStationIndex + 1) % numberOfStations;
        }
        return destinationStationIndex;
    }
}
