package com.rumi.buildingmanagement.infrastructure.web.dto;

import com.rumi.buildingmanagement.domain.model.SensorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "SensorStatusRequest", description = "New status of a sensor")
public record SensorStatusRequestDto(
        @Schema(description = "Status to set", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        SensorStatus status
) {
}
