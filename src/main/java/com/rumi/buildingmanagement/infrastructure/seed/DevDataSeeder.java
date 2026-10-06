package com.rumi.buildingmanagement.infrastructure.seed;

import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingProfile;
import com.rumi.buildingmanagement.domain.model.BuildingStatus;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.model.SensorStatus;
import com.rumi.buildingmanagement.domain.model.SensorType;
import com.rumi.buildingmanagement.domain.repository.BuildingRepository;
import com.rumi.buildingmanagement.domain.repository.SensorRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Loads one active building with one active sensor when the service runs with the dev profile.
 * The identifiers are fixed because the sample data of the other Rumi services refers to them.
 */
@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    public static final UUID BUILDING_ID = UUID.fromString("7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d");
    public static final UUID SENSOR_ID = UUID.fromString("5d1c2f0e-8f4a-4a53-9a7e-0f3b1c9d7a11");
    public static final UUID ADMINISTRATOR_USER_ID = UUID.fromString("c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f");

    private final BuildingRepository buildingRepository;
    private final SensorRepository sensorRepository;

    public DevDataSeeder(BuildingRepository buildingRepository, SensorRepository sensorRepository) {
        this.buildingRepository = buildingRepository;
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (buildingRepository.existsById(BUILDING_ID)) {
            return;
        }
        BuildingProfile profile = new BuildingProfile(
                "Torre Miraflores", "Av. Larco 1234, Miraflores", 12, 2015, 48);
        buildingRepository.save(new Building(BUILDING_ID, profile, BuildingStatus.ACTIVE, ADMINISTRATOR_USER_ID));
        sensorRepository.save(new Sensor(
                SENSOR_ID, BUILDING_ID, "FLOOR-3-NORTH", SensorType.ACCELEROMETER, SensorStatus.ACTIVE));
    }
}
