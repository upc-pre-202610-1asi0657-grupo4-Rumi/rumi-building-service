package com.rumi.buildingmanagement.domain.repository;

import com.rumi.buildingmanagement.domain.model.Building;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository {

    Optional<Building> findById(UUID id);

    List<Building> findAll();

    List<Building> findByAdministratorUserId(UUID administratorUserId);

    Building save(Building building);

    boolean existsById(UUID id);
}
