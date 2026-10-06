package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingProfile;
import com.rumi.buildingmanagement.infrastructure.web.dto.BuildingRequestDto;
import com.rumi.buildingmanagement.infrastructure.web.dto.BuildingResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BuildingDtoMapper {

    public Building toDomain(UUID buildingId, BuildingRequestDto request) {
        BuildingProfile profile = new BuildingProfile(
                request.name(),
                request.address(),
                request.floors(),
                request.constructionYear(),
                request.units()
        );
        return Building.register(buildingId, profile, request.administratorUserId());
    }

    public BuildingResponseDto toResponse(Building building) {
        BuildingProfile profile = building.getProfile();
        return new BuildingResponseDto(
                building.getId(),
                profile.name(),
                profile.address(),
                profile.floors(),
                profile.constructionYear(),
                profile.units(),
                building.getStatus(),
                building.getAdministratorUserId()
        );
    }
}
