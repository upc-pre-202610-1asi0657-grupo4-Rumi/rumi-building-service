package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import com.rumi.buildingmanagement.infrastructure.web.dto.InvitationRequestDto;
import com.rumi.buildingmanagement.infrastructure.web.dto.InvitationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invitations")
@Tag(name = "Invitations", description = "Invitation codes that let residents join a building")
public class InvitationController {

    private final BuildingApplicationService buildingService;
    private final InvitationDtoMapper mapper;

    public InvitationController(BuildingApplicationService buildingService, InvitationDtoMapper mapper) {
        this.buildingService = buildingService;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Invite a resident to a building",
            description = "Generates an invitation with a unique random code for the building (US06). "
                    + "The invitation starts as PENDING and the administrator shares the code with the resident."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Building the resident is invited to",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvitationRequestDto.class),
                    examples = @ExampleObject(name = "Invitation", value = OpenApiExamples.INVITATION_REQUEST)
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Invitation created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvitationResponseDto.class),
                    examples = @ExampleObject(
                            name = "Pending invitation",
                            value = OpenApiExamples.INVITATION_RESPONSE
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "buildingId is missing or is not a valid UUID",
            content = @Content(
                    mediaType = OpenApiExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                            name = "Missing building id",
                            value = OpenApiExamples.INVITATION_VALIDATION_ERROR
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "No building has that identifier",
            content = @Content(
                    mediaType = OpenApiExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                            name = "Unknown building",
                            value = OpenApiExamples.INVITATION_BUILDING_NOT_FOUND_ERROR
                    )
            )
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponseDto inviteResident(@Valid @RequestBody InvitationRequestDto request) {
        ResidentInvitation invitation = buildingService.inviteResident(request.buildingId());
        return mapper.toResponse(invitation);
    }
}
