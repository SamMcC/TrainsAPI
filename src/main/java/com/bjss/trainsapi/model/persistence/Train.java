package com.bjss.trainsapi.model.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@With
@ToString
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class Train {
    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name= "name")
    private String name;

    @OneToOne
    private Schedule currentSchedule;

    @OneToMany(targetEntity = Schedule.class, mappedBy = "train")
    @ToString.Exclude
    private List<Schedule> schedules;

    @Column
    private int speed;

    @Column
    private double distanceToDestination;

    @Column
    private DepartureState departureState;

    @JoinColumn
    @ManyToOne
    private Station currentLocation;

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    @Transient
    @JsonIgnore
    public boolean isReadyToDepart() {
        return this.departureState.equals(DepartureState.READY);
    }

    @Transient
    public boolean hasArrived() {
        return this.departureState.equals(DepartureState.ARRIVED);
    }

    @Transient
    public boolean hasDeparted() {
        return this.departureState.equals(DepartureState.DEPARTED);
    }

    @Transient
    @JsonIgnore
    public String getCurrentTrainDesignation() {
        try {
            String departureTime = currentSchedule.getScheduledDepartureTime().truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ISO_LOCAL_TIME);
            String destinationStation = currentSchedule.getDestination().getName();
            String departureStation = currentSchedule.getSource().getName();
            String delayIndicator = currentSchedule.arrivalIsDelayed() || currentSchedule.departureIsDelayed() ? "Delayed " : "";
            return String.format("%s%s from %s to %s", delayIndicator, departureTime, departureStation, destinationStation);
        } catch(NullPointerException ignored) {
            return null;
        }
    }

    public enum DepartureState {
        READY("Ready for departure"),
        DEPARTED("Departed"),
        ARRIVED("Arrived at destination");

        final String displayName;
        DepartureState(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Train train = (Train) o;
        return id != null && Objects.equals(id, train.id) && Objects.equals(name, train.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
