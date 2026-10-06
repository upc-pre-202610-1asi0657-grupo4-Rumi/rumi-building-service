package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.domain.model.SensorStatus;
import com.rumi.buildingmanagement.domain.model.SensorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "sensors")
public class SensorJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private BuildingJpaEntity building;

    @Column(nullable = false, length = 50)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SensorType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SensorStatus status;

    protected SensorJpaEntity() {
    }

    public SensorJpaEntity(UUID id, BuildingJpaEntity building, String zone, SensorType type, SensorStatus status) {
        this.id = id;
        this.building = building;
        this.zone = zone;
        this.type = type;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public BuildingJpaEntity getBuilding() {
        return building;
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
}
