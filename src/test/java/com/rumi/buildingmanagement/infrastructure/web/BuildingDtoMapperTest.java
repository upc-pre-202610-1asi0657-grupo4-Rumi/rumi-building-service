package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.BuildingFixtures;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingStatus;
import com.rumi.buildingmanagement.infrastructure.web.dto.BuildingRequestDto;
import com.rumi.buildingmanagement.infrastructure.web.dto.BuildingResponseDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BuildingDtoMapperTest {

    @Test
    void mapsBetweenApiDtosAndTheBuildingDomainModel() {
        BuildingDtoMapper mapper = new BuildingDtoMapper();
        UUID buildingId = UUID.randomUUID();
        BuildingRequestDto request = new BuildingRequestDto(
                "Torre Miraflores", "Av. Larco 1234, Miraflores", 12, 2015, 48, BuildingFixtures.ADMINISTRATOR_ID);

        Building building = mapper.toDomain(buildingId, request);
        BuildingResponseDto response = mapper.toResponse(building);

        assertThat(building.getId()).isEqualTo(buildingId);
        assertThat(building.getProfile()).isEqualTo(BuildingFixtures.profile());
        assertThat(building.getStatus()).isEqualTo(BuildingStatus.PENDING_SENSORS);
        assertThat(response).isEqualTo(new BuildingResponseDto(
                buildingId,
                "Torre Miraflores",
                "Av. Larco 1234, Miraflores",
                12,
                2015,
                48,
                BuildingStatus.PENDING_SENSORS,
                BuildingFixtures.ADMINISTRATOR_ID
        ));
    }
}
