package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.model.persistence.Train;
import com.bjss.trainsapi.model.repository.TrainRepository;
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
public class TrainControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    TrainRepository trainRepository;
    
    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.reset();
        RestAssuredMockMvc.mockMvc(mockMvc);
        trainRepository.deleteAll();
    }

    @Test
    void testCreatesTrainWithPost() {
        Train train = Train.builder().departureState(Train.DepartureState.READY).build();

        MockMvcResponse response = createTrain(train);
        response.then()
                .body("departureState", is(equalTo("READY")))
                .and()
                .statusCode(201);
    }


    @Test
    void testGetTrain_ReturnsTrain_whenTrainExists() {
        Train train = Train.builder().departureState(Train.DepartureState.READY).build();

        MockMvcResponse response = createTrain(train);
        Train createdTrain = response.getBody().as(Train.class);

        given()
                .get("http://localhost:" + port + "/api/trains/" + createdTrain.getId())
                .then()
                .contentType(ContentType.JSON)
                .body(
                        "departureState", is(equalTo("READY"))
                ).and()
                .statusCode(
                        is(equalTo(200))
                );
    }


    @Test
    void testGetTrain_ReturnsError_whenTrainDoesNotExist() {
        given()
                .get("http://localhost:" + port + "/api/trains/100")
                .then()
                .contentType(ContentType.JSON)
                .body(
                        "error", is(equalTo("Entity not found"))
                ).and()
                .statusCode(
                        is(equalTo(404))
                );
    }


    @Test
    void testDeletesTrainWithDelete() {
        Train train = Train.builder().departureState(Train.DepartureState.READY).build();

        MockMvcResponse response = createTrain(train);
        Train createdTrain = response.getBody().as(Train.class);

        given()
                .delete("http://localhost:" + port + "/api/trains/" + createdTrain.getId())
                .then()
                .statusCode(
                        is(equalTo(200))
                );
    }

    @Test
    void testUpdatesTrainWithPut() {
        Train train = Train.builder().departureState(Train.DepartureState.READY).build();

        MockMvcResponse response = createTrain(train);
        Train createdTrain = response.getBody().as(Train.class);
        Train updatedTrain = Train.builder().id(createdTrain.getId()).speed(100).departureState(Train.DepartureState.READY).build();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updatedTrain)
                .put("http://localhost:" + port + "/api/trains/" + createdTrain.getId())
                .then()
                .body("speed", is(equalTo(100)))
                .and()
                .statusCode(
                        is(equalTo(200))
                );
    }

    private MockMvcResponse createTrain(Train train) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(train)
                .post("http://localhost:" + port + "/api/trains")
                .thenReturn();
    }
    
}
