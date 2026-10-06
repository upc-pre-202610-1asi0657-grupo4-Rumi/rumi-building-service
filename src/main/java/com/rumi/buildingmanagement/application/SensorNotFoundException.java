package com.rumi.buildingmanagement.application;

import java.util.UUID;

public class SensorNotFoundException extends ResourceNotFoundException {

    public SensorNotFoundException(UUID sensorId) {
        super("Sensor " + sensorId + " was not found");
    }
}
