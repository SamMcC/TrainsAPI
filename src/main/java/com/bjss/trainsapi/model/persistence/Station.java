package com.bjss.trainsapi.model.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Jacksonized
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
//TODO: Fix JSON depth to include more information
public class Station {
    //todo: stations should have neighbouring destination stations,
    // also should introduce a new model object for `Route` which
    // links stations together logically
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    @Convert(converter = CoordinatesConverter.class)
    @Builder.Default
    private Coordinates coordinates = Coordinates.builder().build();

    @OneToMany(targetEntity = Train.class, mappedBy = "currentLocation")
    @JsonIgnore
    private Set<Train> inactiveTrains;

    @OneToMany(targetEntity = Schedule.class, mappedBy = "destination")
    @JsonIgnore
    private Set<Schedule> scheduledArrivals;

    @OneToMany(targetEntity = Schedule.class, mappedBy = "source")
    @JsonIgnore
    private Set<Schedule> scheduledDepartures;

    @Transient
    @JsonIgnore
    public Set<Schedule> getDelayedDepartures() {
        if(scheduledDepartures == null) {
            return null;
        }
        return scheduledDepartures.stream()
            .filter(Schedule::departureIsDelayed)
            .sorted(
                    Comparator.comparing(Schedule::getScheduledDepartureTime)
            ).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transient
    @JsonIgnore
    public Set<Schedule> getDelayedArrivals() {
        if(scheduledArrivals == null) {
            return null;
        }
        return scheduledArrivals.stream()
                .filter(Schedule::arrivalIsDelayed)
                .sorted(
                        Comparator.comparing(Schedule::getScheduledDepartureTime)
                ).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Station station = (Station) o;

        return Objects.equals(id, station.id) && Objects.equals(name, station.name) && Objects.equals(coordinates, station.coordinates);
    }

    @Override
    public int hashCode() {
        return 1719575920;
    }
}
