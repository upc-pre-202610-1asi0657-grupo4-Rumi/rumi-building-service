package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.infrastructure.web.dto.BuildingRequestDto;
import com.rumi.buildingmanagement.infrastructure.web.dto.BuildingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buildings")
@Tag(name = "Buildings", description = "Registration and management of monitored buildings")
public class BuildingController {

    private final BuildingApplicationService buildingService;
    private final BuildingDtoMapper mapper;

    public BuildingController(BuildingApplicationService buildingService, BuildingDtoMapper mapper) {
        this.buildingService = buildingService;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Register a building",
            description = "Registers a new building to be monitored (US04). "
                    + "The service generates the building id and sets the status to PENDING_SENSORS; "
                    + "the building becomes ACTIVE when its first sensor is activated. "
                    + "The administrator is sent in the body until the IAM service exists."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Data of the building to register",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BuildingRequestDto.class),
                    examples = @ExampleObject(name = "Residential tower", value = OpenApiExamples.BUILDING_REQUEST)
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Building registered",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BuildingResponseDto.class),
                    examples = @ExampleObject(name = "Registered building", value = OpenApiExamples.BUILDING_RESPONSE)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "A field is missing, blank or out of range, or the body is not valid JSON",
            content = @Content(
                    mediaType = OpenApiExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Invalid fields", value = OpenApiExamples.BUILDING_VALIDATION_ERROR),
                            @ExampleObject(name = "Malformed body", value = OpenApiExamples.BUILDING_MALFORMED_BODY)
                    }
            )
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuildingResponseDto registerBuilding(@Valid @RequestBody BuildingRequestDto request) {
        Building building = mapper.toDomain(UUID.randomUUID(), request);
        Building savedBuilding = buildingService.registerBuilding(building);
        return mapper.toResponse(savedBuilding);
    }

    @Operation(
            summary = "List buildings",
            description = "Returns the registered buildings ordered by name (US04). "
                    + "When administratorUserId is sent, only the buildings of that administrator are returned. "
                    + "The list is empty when nothing matches."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Buildings found (possibly none)",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = BuildingResponseDto.class)),
                    examples = @ExampleObject(name = "One building", value = OpenApiExamples.BUILDING_LIST_RESPONSE)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "administratorUserId is not a valid UUID",
            content = @Content(
                    mediaType = OpenApiExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                            name = "Invalid administrator id",
                            value = OpenApiExamples.INVALID_ADMINISTRATOR_ID_ERROR
                    )
            )
    )
    @GetMapping
    public List<BuildingResponseDto> listBuildings(
            @Parameter(
                    description = "Returns only the buildings registered by this administrator user",
                    example = "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"
            )
            @RequestParam(required = false) UUID administratorUserId
    ) {
        return buildingService.listBuildings(administratorUserId).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
