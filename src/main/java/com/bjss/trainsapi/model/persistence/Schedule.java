package com.bjss.trainsapi.model.persistence;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Jacksonized
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime scheduledDepartureTime;

    @Column(nullable = false)
    private LocalDateTime scheduledArrivalTime;

    @Column
    private LocalDateTime actualDepartureTime;

    @Column
    private LocalDateTime actualArrivalTime;

    @ManyToOne(targetEntity = Station.class)
    private Station source;

    @ManyToOne(targetEntity = Station.class)
    private Station destination;

    @ManyToOne(targetEntity = Train.class)
    private Train train;

    public boolean departureIsDelayed() {
        if (this.actualDepartureTime == null) {
            return false;
        }
        return this.scheduledDepartureTime.isBefore(this.actualDepartureTime);
    }

    public boolean arrivalIsDelayed() {
        if (this.actualArrivalTime == null) {
            return false;
        }
        return this.scheduledArrivalTime.isBefore(this.actualArrivalTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Schedule schedule = (Schedule) o;

        return Objects.equals(id, schedule.id);
    }

    @Override
    public int hashCode() {
        return 1998924127;
    }
}
