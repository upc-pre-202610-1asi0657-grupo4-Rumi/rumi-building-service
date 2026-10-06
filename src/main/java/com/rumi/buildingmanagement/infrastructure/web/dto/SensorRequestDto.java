package com.rumi.buildingmanagement.infrastructure.web.dto;

import com.rumi.buildingmanagement.domain.model.SensorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "SensorRequest", description = "Data required to register a sensor in a building")
public record SensorRequestDto(
        @Schema(description = "Zone of the building where the sensor is installed", example = "FLOOR-3-NORTH",
                maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 50)
        String zone,
        @Schema(description = "Kind of sensor", example = "ACCELEROMETER",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        SensorType type
) {
}
