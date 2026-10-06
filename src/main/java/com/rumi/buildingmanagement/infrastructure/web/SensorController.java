package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.infrastructure.web.dto.SensorRequestDto;
import com.rumi.buildingmanagement.infrastructure.web.dto.SensorResponseDto;
import com.rumi.buildingmanagement.infrastructure.web.dto.SensorStatusRequestDto;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Sensors", description = "IoT sensors installed in the zones of a building")
public class SensorController {

    private final BuildingApplicationService buildingService;
    private final SensorDtoMapper mapper;

    public SensorController(BuildingApplicationService buildingService, SensorDtoMapper mapper) {
        this.buildingService = buildingService;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Register a sensor in a building",
            description = "Registers a sensor in one zone of the building (US08). "
                    + "The service generates the sensor id and the sensor starts as PENDING."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Zone and type of the sensor to register",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SensorRequestDto.class),
                    examples = @ExampleObject(name = "Accelerometer", value = OpenApiExamples.SENSOR_REQUEST)
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Sensor registered",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SensorResponseDto.class),
                    examples = @ExampleObject(name = "Registered sensor", value = OpenApiExamples.SENSOR_RESPONSE)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "The zone is blank or too long, the type is missing or unknown, "
                    + "or buildingId is not a valid UUID",
            content = @Content(
                    mediaType = OpenApiExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(name = "Blank zone", value = OpenApiExamples.SENSOR_VALIDATION_ERROR)
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
                            value = OpenApiExamples.SENSOR_BUILDING_NOT_FOUND_ERROR
                    )
            )
    )
    @PostMapping("/buildings/{buildingId}/sensors")
    @ResponseStatus(HttpStatus.CREATED)
    public SensorResponseDto registerSensor(
            @Parameter(description = "Identifier of the building that receives the sensor",
                    example = "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d")
            @PathVariable UUID buildingId,
            @Valid @RequestBody SensorRequestDto request
    ) {
        Sensor sensor = buildingService.registerSensor(buildingId, request.zone(), request.type());
        return mapper.toResponse(sensor);
    }

    @Operation(
            summary = "List the sensors of a building",
            description = "Returns the sensors registered in the building, ordered by zone (US08). "
                    + "The list is empty when the building has no sensors yet."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sensors of the building (possibly none)",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = SensorResponseDto.class)),
                    examples = @ExampleObject(name = "Two sensors", value = OpenApiExamples.SENSOR_LIST_RESPONSE)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "buildingId is not a valid UUID",
            content = @Content(
                    mediaType = OpenApiExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                            name = "Invalid building id",
                            value = OpenApiExamples.SENSOR_LIST_INVALID_BUILDING_ID_ERROR
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
                            value = OpenApiExamples.SENSOR_BUILDING_NOT_FOUND_ERROR
                    )
            )
    )
    @GetMapping("/buildings/{buildingId}/sensors")
    public List<SensorResponseDto> listSensors(
            @Parameter(description = "Identifier of the building",
                    example = "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d")
            @PathVariable UUID buildingId
    ) {
        return buildingService.listSensors(buildingId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Operation(
            summary = "Update the status of a sensor",
            description = "Sets the sensor to PENDING, ACTIVE or INACTIVE (US08). "
                    + "A sensor becomes ACTIVE with its first reading; until the readings arrive by messaging, "
                    + "this endpoint does it by hand. When the first sensor of a building becomes ACTIVE, "
                    + "the building moves from PENDING_SENSORS to ACTIVE."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Status to set on the sensor",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SensorStatusRequestDto.class),
                    examples = @ExampleObject(name = "Activate", value = OpenApiExamples.SENSOR_STATUS_REQUEST)
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sensor updated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SensorResponseDto.class),
                    examples = @ExampleObject(name = "Active sensor", value = OpenApiExamples.SENSOR_ACTIVE_RESPONSE)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "The status is missing or is not PENDING, ACTIVE or INACTIVE, "
                    + "or sensorId is not a valid UUID",
            content = @Content(
                    mediaType = OpenApiExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(
                                    name = "Missing status",
                                    value = OpenApiExamples.SENSOR_STATUS_VALIDATION_ERROR
                            ),
                            @ExampleObject(
                                    name = "Unknown status",
                                    value = OpenApiExamples.SENSOR_STATUS_UNKNOWN_VALUE_ERROR
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "No sensor has that identifier",
            content = @Content(
                    mediaType = OpenApiExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(name = "Unknown sensor", value = OpenApiExamples.SENSOR_NOT_FOUND_ERROR)
            )
    )
    @PatchMapping("/sensors/{sensorId}/status")
    public SensorResponseDto updateSensorStatus(
            @Parameter(description = "Identifier of the sensor",
                    example = "5d1c2f0e-8f4a-4a53-9a7e-0f3b1c9d7a11")
            @PathVariable UUID sensorId,
            @Valid @RequestBody SensorStatusRequestDto request
    ) {
        return mapper.toResponse(buildingService.updateSensorStatus(sensorId, request.status()));
    }
}
