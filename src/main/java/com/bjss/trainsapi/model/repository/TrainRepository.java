package com.bjss.trainsapi.model.repository;

import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.persistence.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findBySchedulesContaining(@Param("schedule") Schedule schedule);
}
