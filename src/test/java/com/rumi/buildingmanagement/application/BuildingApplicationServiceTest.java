package com.rumi.buildingmanagement.application;

import com.rumi.buildingmanagement.BuildingFixtures;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.repository.BuildingRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BuildingApplicationServiceTest {

    private final BuildingRepository repository = new InMemoryBuildingRepository();
    private final BuildingApplicationService service = new BuildingApplicationService(repository);

    @Test
    void registersAndFindsABuildingThroughTheDomainRepository() {
        Building building = BuildingFixtures.pendingBuilding();

        service.registerBuilding(building);

        assertThat(service.findBuildingById(building.getId())).containsSame(building);
    }

    @Test
    void listsEveryRegisteredBuilding() {
        Building first = service.registerBuilding(BuildingFixtures.pendingBuilding());
        Building second = service.registerBuilding(BuildingFixtures.pendingBuilding());

        assertThat(service.listBuildings(null)).containsExactly(first, second);
    }

    @Test
    void listsOnlyTheBuildingsOfAnAdministrator() {
        UUID otherAdministrator = UUID.randomUUID();
        service.registerBuilding(BuildingFixtures.pendingBuilding());
        Building owned = service.registerBuilding(
                Building.register(UUID.randomUUID(), BuildingFixtures.profile(), otherAdministrator));

        assertThat(service.listBuildings(otherAdministrator)).containsExactly(owned);
    }
}
