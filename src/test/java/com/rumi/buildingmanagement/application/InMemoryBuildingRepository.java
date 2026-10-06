package com.rumi.buildingmanagement.application;

import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.repository.BuildingRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

final class InMemoryBuildingRepository implements BuildingRepository {

    private final Map<UUID, Building> buildings = new LinkedHashMap<>();

    @Override
    public Optional<Building> findById(UUID id) {
        return Optional.ofNullable(buildings.get(id));
    }

    @Override
    public List<Building> findAll() {
        return List.copyOf(buildings.values());
    }

    @Override
    public List<Building> findByAdministratorUserId(UUID administratorUserId) {
        return buildings.values().stream()
                .filter(building -> building.getAdministratorUserId().equals(administratorUserId))
                .toList();
    }

    @Override
    public Building save(Building building) {
        buildings.put(building.getId(), building);
        return building;
    }

    @Override
    public boolean existsById(UUID id) {
        return buildings.containsKey(id);
    }
}
