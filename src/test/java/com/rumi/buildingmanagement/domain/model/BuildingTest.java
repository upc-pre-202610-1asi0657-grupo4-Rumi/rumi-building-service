package com.rumi.buildingmanagement.domain.model;

import com.rumi.buildingmanagement.BuildingFixtures;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class BuildingTest {

    @Test
    void aRegisteredBuildingWaitsForItsSensors() {
        Building building = Building.register(
                UUID.randomUUID(), BuildingFixtures.profile(), BuildingFixtures.ADMINISTRATOR_ID);

        assertThat(building.getStatus()).isEqualTo(BuildingStatus.PENDING_SENSORS);
        assertThat(building.getName()).isEqualTo("Torre Miraflores");
        assertThat(building.getAdministratorUserId()).isEqualTo(BuildingFixtures.ADMINISTRATOR_ID);
    }

    @Test
    void rejectsABuildingWithoutAdministrator() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Building.register(UUID.randomUUID(), BuildingFixtures.profile(), null));
    }

    @Test
    void rejectsABlankNameOrAddress() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new BuildingProfile(" ", "Av. Larco 1234", 12, 2015, 48));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new BuildingProfile("Torre Miraflores", "", 12, 2015, 48));
    }

    @Test
    void rejectsANameLongerThanOneHundredCharacters() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new BuildingProfile("x".repeat(101), "Av. Larco 1234", 12, 2015, 48));
    }

    @Test
    void rejectsNonPositiveFloorsAndUnits() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new BuildingProfile("Torre Miraflores", "Av. Larco 1234", 0, 2015, 48));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new BuildingProfile("Torre Miraflores", "Av. Larco 1234", 12, 2015, 0));
    }
}
