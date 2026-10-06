package com.rumi.buildingmanagement.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "InvitationRequest", description = "Data required to invite a resident to a building")
public record InvitationRequestDto(
        @Schema(description = "Identifier of the building the resident is invited to",
                example = "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID buildingId
) {
}
