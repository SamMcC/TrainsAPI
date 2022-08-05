package com.bjss.trainsapi.model.repository;

import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.persistence.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.destination = :station OR s.source = :station")
    List<Schedule> findByStation(@Param("station") Station station);

    Optional<Schedule> findFirstByTrainAndScheduledDepartureTimeAfterOrderByScheduledDepartureTimeAsc(
            @Param("train")
            Train train,

            @Param("scheduledDepartureTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime scheduledDepartureTime
    );
}
