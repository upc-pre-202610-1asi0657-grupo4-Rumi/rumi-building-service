package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.BuildingFixtures;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.model.SensorStatus;
import com.rumi.buildingmanagement.domain.model.SensorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import({JpaBuildingRepository.class, JpaSensorRepository.class})
class JpaSensorRepositoryTest {

    @Autowired
    private JpaBuildingRepository buildingRepository;

    @Autowired
    private JpaSensorRepository sensorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesASensorLinkedToItsBuilding() {
        Building building = buildingRepository.save(BuildingFixtures.pendingBuilding());
        Sensor sensor = building.registerSensor("FLOOR-3-NORTH", SensorType.ACCELEROMETER);

        Sensor saved = sensorRepository.save(sensor);
        entityManager.flush();
        entityManager.clear();

        SensorJpaEntity stored = entityManager.find(SensorJpaEntity.class, sensor.getId());
        assertThat(saved.getBuildingId()).isEqualTo(building.getId());
        assertThat(stored.getBuilding().getId()).isEqualTo(building.getId());
        assertThat(stored.getZone()).isEqualTo("FLOOR-3-NORTH");
        assertThat(stored.getType()).isEqualTo(SensorType.ACCELEROMETER);
        assertThat(stored.getStatus()).isEqualTo(SensorStatus.PENDING);
    }
}
