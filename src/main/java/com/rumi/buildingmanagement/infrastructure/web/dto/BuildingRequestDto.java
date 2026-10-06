package com.rumi.buildingmanagement.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(name = "BuildingRequest", description = "Data required to register a building")
public record BuildingRequestDto(
        @Schema(description = "Name of the building", example = "Torre Miraflores", maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 100)
        String name,
        @Schema(description = "Street address of the building", example = "Av. Larco 1234, Miraflores",
                maxLength = 200, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 200)
        String address,
        @Schema(description = "Number of floors", example = "12", minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Min(1)
        Integer floors,
        @Schema(description = "Year the building was built", example = "2015", minimum = "1800", maximum = "2100",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Min(1800)
        @Max(2100)
        Integer constructionYear,
        @Schema(description = "Number of housing units", example = "48", minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Min(1)
        Integer units,
        @Schema(description = "Identifier of the administrator user who registers the building",
                example = "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID administratorUserId
) {
}
