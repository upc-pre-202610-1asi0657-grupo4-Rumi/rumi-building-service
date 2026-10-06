package com.rumi.buildingmanagement.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SensorTest {

    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Test
    void aRegisteredSensorIsPending() {
        Sensor sensor = Sensor.register(UUID.randomUUID(), BUILDING_ID, "FLOOR-3-NORTH", SensorType.INCLINOMETER);

        assertThat(sensor.getStatus()).isEqualTo(SensorStatus.PENDING);
        assertThat(sensor.getBuildingId()).isEqualTo(BUILDING_ID);
    }

    @Test
    void rejectsABlankZone() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Sensor.register(UUID.randomUUID(), BUILDING_ID, " ", SensorType.ACCELEROMETER));
    }

    @Test
    void rejectsAZoneLongerThanFiftyCharacters() {
        assertThatIllegalArgumentException().isThrownBy(() -> Sensor.register(
                UUID.randomUUID(), BUILDING_ID, "Z".repeat(51), SensorType.ACCELEROMETER));
    }

    @Test
    void rejectsAMissingType() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Sensor.register(UUID.randomUUID(), BUILDING_ID, "FLOOR-3-NORTH", null));
    }

    @Test
    void activatesAndChangesItsStatus() {
        Sensor sensor = Sensor.register(UUID.randomUUID(), BUILDING_ID, "FLOOR-3-NORTH", SensorType.ACCELEROMETER);

        sensor.activate();
        assertThat(sensor.isActive()).isTrue();

        sensor.changeStatus(SensorStatus.INACTIVE);
        assertThat(sensor.getStatus()).isEqualTo(SensorStatus.INACTIVE);
    }

    @Test
    void rejectsAMissingStatus() {
        Sensor sensor = Sensor.register(UUID.randomUUID(), BUILDING_ID, "FLOOR-3-NORTH", SensorType.ACCELEROMETER);

        assertThatIllegalArgumentException().isThrownBy(() -> sensor.changeStatus(null));
    }
}
