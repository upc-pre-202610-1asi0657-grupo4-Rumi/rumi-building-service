package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.domain.model.InvitationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resident_invitations")
public class ResidentInvitationJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private BuildingJpaEntity building;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected ResidentInvitationJpaEntity() {
    }

    public ResidentInvitationJpaEntity(
            UUID id,
            BuildingJpaEntity building,
            String code,
            InvitationStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.building = building;
        this.code = code;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public BuildingJpaEntity getBuilding() {
        return building;
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
}
