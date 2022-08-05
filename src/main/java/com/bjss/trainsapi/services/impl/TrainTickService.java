package com.bjss.trainsapi.services.impl;

import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.services.IScheduleService;
import com.bjss.trainsapi.services.IStationService;
import com.bjss.trainsapi.services.ITrainService;
import com.bjss.trainsapi.services.ITrainTickService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainTickService implements ITrainTickService {
    private final IScheduleService scheduleService;
    private final ITrainService trainService;
    private final IStationService stationService;

    private static final Function<Double, Duration> calculateTime = (distance) -> Duration.ofMinutes((long) (distance / 40));

    @Override
    public void tickTrainsOnce() {
        List<Train> trains = trainService.findAll();
        List<Train> readyTrains = trains.stream().filter(Train::isReadyToDepart).collect(Collectors.toList());
        List<Train> arrivedTrains = trains.stream().filter(Train::hasArrived).collect(Collectors.toList());
        List<Train> departedTrains = trains.stream().filter(Train::hasDeparted).collect(Collectors.toList());

        handleReadyTrains(readyTrains);
        handleArrivedTrains(arrivedTrains);
        handleDepartedTrains(departedTrains);
    }

    private void handleDepartedTrains(List<Train> departedTrains) {
        for (Train train : departedTrains) {
            int newSpeed = (RandomUtils.nextInt() % 80) + 1;
            double newDistanceToDestination = train.getDistanceToDestination() - newSpeed;
            if (newDistanceToDestination < 0) {
                newDistanceToDestination = 0;
            }
            Schedule schedule = train.getCurrentSchedule();
            schedule.setActualArrivalTime(LocalDateTime.now()
                    .plus(calculateTime.apply(newDistanceToDestination)));
            Train.DepartureState departureState = train.getDepartureState();
            Station currentLocation = null;
            if (newDistanceToDestination == 0) {
                departureState = Train.DepartureState.ARRIVED;
                currentLocation = schedule.getDestination();
            }
            trainService.save(
                    train.withCurrentLocation(currentLocation)
                            .withDepartureState(departureState)
                            .withDistanceToDestination(newDistanceToDestination)
                            .withSpeed(newSpeed)
            );
            scheduleService.save(schedule);
        }
    }

    private void handleArrivedTrains(List<Train> arrivedTrains) {
        for (Train train : arrivedTrains) {
            Schedule nextSchedule = scheduleService.findNextScheduleForTrain(train).orElse(null);
            double distanceToDestination = nextSchedule != null ? nextSchedule.getSource().getCoordinates()
                    .distanceToCoordinate(nextSchedule.getDestination().getCoordinates()) : 0;
            trainService.save(
                    train.withCurrentSchedule(nextSchedule)
                            .withDistanceToDestination(distanceToDestination)
                            .withDepartureState(Train.DepartureState.READY)
            );
        }
    }

    private void handleReadyTrains(List<Train> readyTrains) {
        //   todo: add more requirements so that trains can have delayed departures
        for(Train train : readyTrains) {
            Schedule currentSchedule = train.getCurrentSchedule();
            if (currentSchedule != null) {
                if (currentSchedule.getScheduledDepartureTime().isBefore(LocalDateTime.now().minusSeconds(1))) {
                    double distanceToDestination = currentSchedule.getSource().getCoordinates()
                            .distanceToCoordinate(currentSchedule.getDestination().getCoordinates());
                    LocalDateTime actualDepartureTime = LocalDateTime.now();
                    scheduleService.save(currentSchedule.withActualDepartureTime(actualDepartureTime));
                    trainService.save(train
                            .withDepartureState(Train.DepartureState.DEPARTED)
                            .withDistanceToDestination(distanceToDestination)
                            .withCurrentLocation(null)
                    );
                }
            }
        }
    }
}
