package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.infrastructure.web.dto.BuildingRequestDto;
import com.rumi.buildingmanagement.infrastructure.web.dto.BuildingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
                    + "The service generates the building id and returns the stored building."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Name and address of the building to register",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BuildingRequestDto.class),
                    examples = @ExampleObject(
                            name = "Residential tower",
                            value = """
                                    {
                                      "name": "Rumi Tower",
                                      "address": "Av. Arequipa 1234, Lince, Lima"
                                    }"""
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Building registered",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BuildingResponseDto.class),
                    examples = @ExampleObject(
                            name = "Registered building",
                            value = """
                                    {
                                      "id": "3f2c8a10-5d7b-4e9a-b1c2-0a1b2c3d4e5f",
                                      "name": "Rumi Tower",
                                      "address": "Av. Arequipa 1234, Lince, Lima"
                                    }"""
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "The request body is missing or is not valid JSON",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "Malformed body",
                            value = """
                                    {
                                      "timestamp": "2026-10-06T15:30:00.000+00:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "path": "/api/v1/buildings"
                                    }"""
                    )
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "The name or the address is missing or blank. "
                    + "The domain model rejects it and the error is not yet mapped to a 4xx status.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            name = "Blank name",
                            value = """
                                    {
                                      "timestamp": "2026-10-06T15:30:00.000+00:00",
                                      "status": 500,
                                      "error": "Internal Server Error",
                                      "path": "/api/v1/buildings"
                                    }"""
                    )
            )
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuildingResponseDto registerBuilding(@RequestBody BuildingRequestDto request) {
        Building building = mapper.toDomain(UUID.randomUUID(), request);
        Building savedBuilding = buildingService.registerBuilding(building);
        return mapper.toResponse(savedBuilding);
    }
}
