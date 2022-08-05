package com.bjss.trainsapi.model.repository;

import com.bjss.trainsapi.model.persistence.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByName(String name);
}
