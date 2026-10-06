package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.application.BuildingNotFoundException;
import com.rumi.buildingmanagement.application.SensorNotFoundException;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.model.SensorStatus;
import com.rumi.buildingmanagement.domain.model.SensorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorController.class)
@Import(SensorDtoMapper.class)
class SensorControllerTest {

    private static final UUID BUILDING_ID = UUID.fromString("7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d");
    private static final UUID SENSOR_ID = UUID.fromString("5d1c2f0e-8f4a-4a53-9a7e-0f3b1c9d7a11");
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

    @Test
    void listsTheSensorsOfABuilding() throws Exception {
        Sensor sensor = Sensor.register(UUID.randomUUID(), BUILDING_ID, "FLOOR-3-NORTH", SensorType.ACCELEROMETER);
        when(buildingService.listSensors(BUILDING_ID)).thenReturn(List.of(sensor));

        mockMvc.perform(get("/api/v1/buildings/{buildingId}/sensors", BUILDING_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(sensor.getId().toString()))
                .andExpect(jsonPath("$[0].zone").value("FLOOR-3-NORTH"));
    }

    @Test
    void returnsNotFoundWhenListingTheSensorsOfAnUnknownBuilding() throws Exception {
        when(buildingService.listSensors(BUILDING_ID)).thenThrow(new BuildingNotFoundException(BUILDING_ID));

        mockMvc.perform(get("/api/v1/buildings/{buildingId}/sensors", BUILDING_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void rejectsABuildingIdThatIsNotAUuidWhenListingSensors() throws Exception {
        mockMvc.perform(get("/api/v1/buildings/not-a-uuid/sensors"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.instance").value("/api/v1/buildings/not-a-uuid/sensors"));
    }

    @Test
    void updatesTheStatusOfASensor() throws Exception {
        Sensor sensor = Sensor.register(SENSOR_ID, BUILDING_ID, "FLOOR-3-NORTH", SensorType.ACCELEROMETER);
        sensor.activate();
        when(buildingService.updateSensorStatus(SENSOR_ID, SensorStatus.ACTIVE)).thenReturn(sensor);

        mockMvc.perform(patch("/api/v1/sensors/{sensorId}/status", SENSOR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SENSOR_ID.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rejectsAMissingStatusWithABadRequestProblemDetail() throws Exception {
        mockMvc.perform(patch("/api/v1/sensors/{sensorId}/status", SENSOR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.status").value("must not be null"));

        verifyNoInteractions(buildingService);
    }

    @Test
    void rejectsAnUnknownStatusWithABadRequestProblemDetail() throws Exception {
        mockMvc.perform(patch("/api/v1/sensors/{sensorId}/status", SENSOR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"BROKEN\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Failed to read request"));
    }

    @Test
    void returnsNotFoundForAnUnknownSensor() throws Exception {
        when(buildingService.updateSensorStatus(SENSOR_ID, SensorStatus.ACTIVE))
                .thenThrow(new SensorNotFoundException(SENSOR_ID));

        mockMvc.perform(patch("/api/v1/sensors/{sensorId}/status", SENSOR_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Sensor " + SENSOR_ID + " was not found"));
    }
}
