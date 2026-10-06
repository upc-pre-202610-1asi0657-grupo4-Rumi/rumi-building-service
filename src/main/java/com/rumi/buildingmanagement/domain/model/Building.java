package com.rumi.buildingmanagement.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class Building {

    private final UUID id;
    private final BuildingProfile profile;
    private BuildingStatus status;
    private final UUID administratorUserId;

    public Building(UUID id, BuildingProfile profile, BuildingStatus status, UUID administratorUserId) {
        this.id = Objects.requireNonNull(id, "Building id is required");
        this.profile = Objects.requireNonNull(profile, "Building profile is required");
        this.status = Objects.requireNonNull(status, "Building status is required");
        this.administratorUserId = requireAdministrator(administratorUserId);
    }

    /**
     * Creates a new building, which waits for its sensors before it is monitored.
     */
    public static Building register(UUID id, BuildingProfile profile, UUID administratorUserId) {
        return new Building(id, profile, BuildingStatus.PENDING_SENSORS, administratorUserId);
    }

    /**
     * Adds a sensor to one zone of this building.
     */
    public Sensor registerSensor(String zone, SensorType type) {
        return Sensor.register(UUID.randomUUID(), id, zone, type);
    }

    public UUID getId() {
        return id;
    }

    public BuildingProfile getProfile() {
        return profile;
    }

    public String getName() {
        return profile.name();
    }

    public String getAddress() {
        return profile.address();
    }

    public BuildingStatus getStatus() {
        return status;
    }

    public UUID getAdministratorUserId() {
        return administratorUserId;
    }

    private static UUID requireAdministrator(UUID administratorUserId) {
        if (administratorUserId == null) {
            throw new IllegalArgumentException("Building administrator user id is required");
        }
        return administratorUserId;
    }
}
