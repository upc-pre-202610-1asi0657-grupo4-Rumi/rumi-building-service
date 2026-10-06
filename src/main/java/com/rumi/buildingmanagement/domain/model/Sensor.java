package com.rumi.buildingmanagement.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class Sensor {

    public static final int MAX_ZONE_LENGTH = 50;

    private final UUID id;
    private final UUID buildingId;
    private final String zone;
    private final SensorType type;
    private SensorStatus status;

    public Sensor(UUID id, UUID buildingId, String zone, SensorType type, SensorStatus status) {
        this.id = Objects.requireNonNull(id, "Sensor id is required");
        this.buildingId = Objects.requireNonNull(buildingId, "Sensor building id is required");
        this.zone = requireZone(zone);
        this.type = requireValue(type, "Sensor type is required");
        this.status = requireValue(status, "Sensor status is required");
    }

    /**
     * Creates a new sensor, which stays pending until it is activated.
     */
    public static Sensor register(UUID id, UUID buildingId, String zone, SensorType type) {
        return new Sensor(id, buildingId, zone, type, SensorStatus.PENDING);
    }

    public void activate() {
        changeStatus(SensorStatus.ACTIVE);
    }

    public void changeStatus(SensorStatus newStatus) {
        this.status = requireValue(newStatus, "Sensor status is required");
    }

    public boolean isActive() {
        return status == SensorStatus.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBuildingId() {
        return buildingId;
    }

    public String getZone() {
        return zone;
    }

    public SensorType getType() {
        return type;
    }

    public SensorStatus getStatus() {
        return status;
    }

    private static String requireZone(String zone) {
        if (zone == null || zone.isBlank()) {
            throw new IllegalArgumentException("Sensor zone is required");
        }
        if (zone.length() > MAX_ZONE_LENGTH) {
            throw new IllegalArgumentException("Sensor zone must have at most " + MAX_ZONE_LENGTH + " characters");
        }
        return zone;
    }

    private static <T> T requireValue(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
