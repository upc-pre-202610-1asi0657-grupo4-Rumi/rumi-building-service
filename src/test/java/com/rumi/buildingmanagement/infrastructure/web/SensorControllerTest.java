package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.application.BuildingNotFoundException;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.model.SensorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorController.class)
@Import(SensorDtoMapper.class)
class SensorControllerTest {

    private static final UUID BUILDING_ID = UUID.fromString("7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d");
    private static final String SENSOR_JSON = "{\"zone\":\"FLOOR-3-NORTH\",\"type\":\"ACCELEROMETER\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuildingApplicationService buildingService;

    @Test
    void registersASensorAndReturnsItWithStatusCreated() throws Exception {
        Sensor sensor = Sensor.register(UUID.randomUUID(), BUILDING_ID, "FLOOR-3-NORTH", SensorType.ACCELEROMETER);
        when(buildingService.registerSensor(BUILDING_ID, "FLOOR-3-NORTH", SensorType.ACCELEROMETER))
                .thenReturn(sensor);

        mockMvc.perform(post("/api/v1/buildings/{buildingId}/sensors", BUILDING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SENSOR_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sensor.getId().toString()))
                .andExpect(jsonPath("$.buildingId").value(BUILDING_ID.toString()))
                .andExpect(jsonPath("$.zone").value("FLOOR-3-NORTH"))
                .andExpect(jsonPath("$.type").value("ACCELEROMETER"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void rejectsABlankZoneWithABadRequestProblemDetail() throws Exception {
        mockMvc.perform(post("/api/v1/buildings/{buildingId}/sensors", BUILDING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"zone\":\"\",\"type\":\"ACCELEROMETER\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.zone").value("must not be blank"));

        verifyNoInteractions(buildingService);
    }

    @Test
    void rejectsAnUnknownSensorTypeWithABadRequestProblemDetail() throws Exception {
        mockMvc.perform(post("/api/v1/buildings/{buildingId}/sensors", BUILDING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"zone\":\"FLOOR-3-NORTH\",\"type\":\"THERMOMETER\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void returnsNotFoundWhenTheBuildingOfTheSensorDoesNotExist() throws Exception {
        when(buildingService.registerSensor(BUILDING_ID, "FLOOR-3-NORTH", SensorType.ACCELEROMETER))
                .thenThrow(new BuildingNotFoundException(BUILDING_ID));

        mockMvc.perform(post("/api/v1/buildings/{buildingId}/sensors", BUILDING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SENSOR_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Building " + BUILDING_ID + " was not found"));
    }
}
