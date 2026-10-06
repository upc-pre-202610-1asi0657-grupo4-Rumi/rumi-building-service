package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.infrastructure.web.dto.SensorResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SensorDtoMapper {

    public SensorResponseDto toResponse(Sensor sensor) {
        return new SensorResponseDto(
                sensor.getId(),
                sensor.getBuildingId(),
                sensor.getZone(),
                sensor.getType(),
                sensor.getStatus()
        );
    }
}
