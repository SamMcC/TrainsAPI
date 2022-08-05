package com.bjss.trainsapi.model.persistence;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.Hibernate;

import javax.persistence.*;
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Train> inactiveTrains;

    @OneToMany(targetEntity = Schedule.class, mappedBy = "destination")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Schedule> scheduledArrivals;

    @OneToMany(targetEntity = Schedule.class, mappedBy = "source")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Schedule> scheduledDepartures;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
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
