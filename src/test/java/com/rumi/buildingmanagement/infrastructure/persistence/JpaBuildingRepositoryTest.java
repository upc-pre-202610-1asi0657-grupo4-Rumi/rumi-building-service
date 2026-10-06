package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.BuildingFixtures;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import(JpaBuildingRepository.class)
class JpaBuildingRepositoryTest {

    @Autowired
    private JpaBuildingRepository repository;

    @Test
    void savesAndFindsABuilding() {
        Building building = BuildingFixtures.pendingBuilding();

        repository.save(building);

        assertThat(repository.existsById(building.getId())).isTrue();
        assertThat(repository.findById(building.getId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getProfile()).isEqualTo(BuildingFixtures.profile());
                    assertThat(found.getStatus()).isEqualTo(BuildingStatus.PENDING_SENSORS);
                    assertThat(found.getAdministratorUserId()).isEqualTo(BuildingFixtures.ADMINISTRATOR_ID);
                });
    }

    @Test
    void findsNothingForAnUnknownId() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }
}
