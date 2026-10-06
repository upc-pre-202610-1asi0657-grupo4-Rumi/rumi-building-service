package com.rumi.buildingmanagement;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingStatus;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.model.SensorStatus;
import com.rumi.buildingmanagement.domain.model.SensorType;
import com.rumi.buildingmanagement.infrastructure.seed.DevDataSeeder;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class BuildingManagementServiceApplicationTest {

    @Autowired
    private BuildingApplicationService buildingService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoadsWithTheDevProfileAndNoExternalInfrastructure() {
        assertThat(buildingService).isNotNull();
    }

    @Test
    void theDevProfileSeedsTheSampleBuildingWithItsFixedId() {
        Building building = buildingService.getBuilding(
                UUID.fromString("7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d"));

        assertThat(building.getName()).isEqualTo("Torre Miraflores");
        assertThat(building.getAddress()).isEqualTo("Av. Larco 1234, Miraflores");
        assertThat(building.getStatus()).isEqualTo(BuildingStatus.ACTIVE);
    }

    @Test
    void theDevProfileSeedsTheSampleSensorWithItsFixedId() {
        List<Sensor> sensors = buildingService.listSensors(DevDataSeeder.BUILDING_ID);

        assertThat(sensors).singleElement().satisfies(sensor -> {
            assertThat(sensor.getId()).isEqualTo(UUID.fromString("5d1c2f0e-8f4a-4a53-9a7e-0f3b1c9d7a11"));
            assertThat(sensor.getZone()).isEqualTo("FLOOR-3-NORTH");
            assertThat(sensor.getType()).isEqualTo(SensorType.ACCELEROMETER);
            assertThat(sensor.getStatus()).isEqualTo(SensorStatus.ACTIVE);
        });
    }

    @Test
    void validationMessagesAreInEnglishWhateverTheClientLocale() throws Exception {
        mockMvc.perform(post("/api/v1/buildings")
                        .header("Accept-Language", "es-PE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("must not be blank"));
    }

    @Test
    void activatingTheFirstSensorOfANewBuildingActivatesItEndToEnd() throws Exception {
        String buildingId = JsonPath.read(mockMvc.perform(post("/api/v1/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BuildingFixtures.BUILDING_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_SENSORS"))
                .andReturn().getResponse().getContentAsString(), "$.id");
        String sensorId = JsonPath.read(mockMvc.perform(post("/api/v1/buildings/{buildingId}/sensors", buildingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"zone\":\"ROOF-SOUTH\",\"type\":\"INCLINOMETER\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/v1/sensors/{sensorId}/status", sensorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/v1/buildings/{buildingId}", buildingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
