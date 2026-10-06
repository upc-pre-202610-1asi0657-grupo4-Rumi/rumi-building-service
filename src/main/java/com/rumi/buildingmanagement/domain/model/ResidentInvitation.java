package com.rumi.buildingmanagement.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ResidentInvitation {

    public static final int MAX_CODE_LENGTH = 20;

    private final UUID id;
    private final UUID buildingId;
    private final String code;
    private final InvitationStatus status;
    private final Instant createdAt;

    public ResidentInvitation(UUID id, UUID buildingId, String code, InvitationStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Invitation id is required");
        this.buildingId = Objects.requireNonNull(buildingId, "Invitation building id is required");
        this.code = requireCode(code);
        this.status = Objects.requireNonNull(status, "Invitation status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Invitation creation time is required");
    }

    /**
     * Creates a new invitation, which stays pending until a resident uses its code.
     */
    public static ResidentInvitation generate(UUID id, UUID buildingId, String code, Instant createdAt) {
        return new ResidentInvitation(id, buildingId, code, InvitationStatus.PENDING, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getBuildingId() {
        return buildingId;
    }

    public String getCode() {
        return code;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private static String requireCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Invitation code is required");
        }
        if (code.length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "Invitation code must have at most " + MAX_CODE_LENGTH + " characters");
        }
        return code;
    }
}
