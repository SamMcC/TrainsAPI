package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.model.persistence.Station;
import com.bjss.trainsapi.model.repository.StationRepository;
import io.restassured.http.ContentType;
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

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "test")
public class StationControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StationRepository stationRepository;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.reset();
        RestAssuredMockMvc.mockMvc(mockMvc);
        stationRepository.deleteAll();
    }

    @Test
    void testCreatesStationWithPost() {
        Station station = Station.builder().name("Test").build();

        MockMvcResponse response = createStation(station);
        response.then()
                .body("name", is(equalTo("Test")))
                .and()
                .statusCode(201);
    }

    @Test
    void testGetStationReturnsStation() {
        Station station = Station.builder().name("Test").build();

        MockMvcResponse response = createStation(station);
        Station createdStation = response.getBody().as(Station.class);

        given()
            .get("http://localhost:" + port + "/api/stations/" + createdStation.getId())
            .then()
            .contentType(ContentType.JSON)
            .body(
                    "name", is(equalTo("Test"))
            ).and()
            .statusCode(
                    is(equalTo(200))
            );
    }


    @Test
    void testDeletesStationWithDelete() {
        Station station = Station.builder().name("Test").build();

        MockMvcResponse response = createStation(station);
        Station createdStation = response.getBody().as(Station.class);

        given()
                .delete("http://localhost:" + port + "/api/stations/" + createdStation.getId())
                .then()
                .statusCode(
                        is(equalTo(200))
                );
    }

    @Test
    void testUpdatesStationWithPut() {
        Station station = Station.builder().name("Test").build();

        MockMvcResponse response = createStation(station);
        Station createdStation = response.getBody().as(Station.class);
        station.setId(createdStation.getId());
        station.setName("Updated");

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(station)
                .put("http://localhost:" + port + "/api/stations/" + createdStation.getId())
                .then()
                .body("name", is(equalTo("Updated")))
                .and()
                .statusCode(
                        is(equalTo(200))
                );
    }
    private MockMvcResponse createStation(Station station) {
        return given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(station)
                    .post("http://localhost:" + port + "/api/stations")
                    .thenReturn();
    }

}
