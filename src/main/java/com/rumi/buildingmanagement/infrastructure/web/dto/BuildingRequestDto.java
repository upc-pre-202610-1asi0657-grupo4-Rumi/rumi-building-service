package com.rumi.buildingmanagement.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BuildingRequest", description = "Data required to register a building")
public record BuildingRequestDto(
        @Schema(description = "Name of the building", example = "Rumi Tower",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Street address of the building", example = "Av. Arequipa 1234, Lince, Lima",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String address
) {
}
