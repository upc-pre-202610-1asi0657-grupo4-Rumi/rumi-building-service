package com.rumi.buildingmanagement;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingStatus;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.model.SensorStatus;
import com.rumi.buildingmanagement.domain.model.SensorType;
import com.rumi.buildingmanagement.infrastructure.seed.DevDataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class BuildingManagementServiceApplicationTest {

    @Autowired
    private BuildingApplicationService buildingService;

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
}
