package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.model.repository.ScheduleRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "test")
public class ScheduleControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ScheduleRepository scheduleRepository;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.reset();
        RestAssuredMockMvc.mockMvc(mockMvc);
        scheduleRepository.deleteAll();
    }

    @Test
    void testPost_CreatesSchedule() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        Schedule schedule = Schedule.builder().scheduledArrivalTime(now).scheduledDepartureTime(now).build();

        MockMvcResponse response = createSchedule(schedule);
        Schedule getSchedule = response.then()
                .statusCode(201)
                .extract().as(Schedule.class);

        assertThat(getSchedule.getScheduledArrivalTime()).isEqualToIgnoringNanos(now);
    }


    @Test
    void testGetScheduleReturnsSchedule() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        Schedule schedule = Schedule.builder().scheduledArrivalTime(now).scheduledDepartureTime(now).build();

        Schedule createdSchedule = createSchedule(schedule).as(Schedule.class);

        Schedule getSchedule = given()
                .get("http://localhost:" + port + "/api/schedules/" + createdSchedule.getId())
                .then()
                .statusCode(
                        is(equalTo(200))
                ).extract().as(Schedule.class);

        assertThat(getSchedule.getScheduledDepartureTime()).isEqualToIgnoringNanos(now);
    }


    @Test
    void testDeletesScheduleWithDelete() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        Schedule schedule = Schedule.builder().scheduledArrivalTime(now).scheduledDepartureTime(now).build();

        Schedule createdSchedule = createSchedule(schedule).as(Schedule.class);

        given()
                .delete("http://localhost:" + port + "/api/schedules/" + createdSchedule.getId())
                .then()
                .statusCode(
                        is(equalTo(200))
                );
    }

    @Test
    void testUpdatesScheduleWithPut() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        Schedule schedule = Schedule.builder().scheduledArrivalTime(now).scheduledDepartureTime(now).build();

        Schedule createdSchedule = createSchedule(schedule).as(Schedule.class);
        schedule.setId(createdSchedule.getId());
        schedule.setActualDepartureTime(now);

        Schedule getSchedule = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(schedule)
                .put("http://localhost:" + port + "/api/schedules/" + createdSchedule.getId())
                .then()
                .statusCode(
                        is(equalTo(200))
                ).extract().as(Schedule.class);

        assertThat(getSchedule.getActualDepartureTime()).isEqualToIgnoringNanos(now);
    }

    private MockMvcResponse createSchedule(Schedule schedule) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(schedule)
                .post("http://localhost:" + port + "/api/schedules")
                .thenReturn();
    }
}
