package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.domain.model.Building;
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
        Building building = new Building(UUID.randomUUID(), "Rumi Tower", "Av. Arequipa 1234");

        repository.save(building);

        assertThat(repository.existsById(building.getId())).isTrue();
        assertThat(repository.findById(building.getId()))
                .hasValueSatisfying(found -> {
                    assertThat(found.getName()).isEqualTo("Rumi Tower");
                    assertThat(found.getAddress()).isEqualTo("Av. Arequipa 1234");
                });
    }

    @Test
    void findsNothingForAnUnknownId() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }
}
