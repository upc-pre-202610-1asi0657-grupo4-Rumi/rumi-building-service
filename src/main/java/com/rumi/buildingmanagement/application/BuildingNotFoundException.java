package com.rumi.buildingmanagement.application;

import java.util.UUID;

public class BuildingNotFoundException extends ResourceNotFoundException {

    public BuildingNotFoundException(UUID buildingId) {
        super("Building " + buildingId + " was not found");
    }
}
