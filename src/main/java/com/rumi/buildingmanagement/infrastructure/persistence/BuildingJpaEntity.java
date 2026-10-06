package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.domain.model.BuildingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "buildings")
public class BuildingJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false)
    private int floors;

    @Column(nullable = false)
    private int constructionYear;

    @Column(nullable = false)
    private int units;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BuildingStatus status;

    @Column(nullable = false)
    private UUID administratorUserId;

    protected BuildingJpaEntity() {
    }

    public BuildingJpaEntity(
            UUID id,
            String name,
            String address,
            int floors,
            int constructionYear,
            int units,
            BuildingStatus status,
            UUID administratorUserId
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.floors = floors;
        this.constructionYear = constructionYear;
        this.units = units;
        this.status = status;
        this.administratorUserId = administratorUserId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getFloors() {
        return floors;
    }

    public int getConstructionYear() {
        return constructionYear;
    }

    public int getUnits() {
        return units;
    }

    public BuildingStatus getStatus() {
        return status;
    }

    public UUID getAdministratorUserId() {
        return administratorUserId;
    }
}
