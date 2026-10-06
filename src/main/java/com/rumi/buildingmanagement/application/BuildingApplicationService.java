package com.rumi.buildingmanagement.application;

import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.repository.BuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BuildingApplicationService {

    private final BuildingRepository buildingRepository;

    public BuildingApplicationService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public Optional<Building> findBuildingById(UUID buildingId) {
        return buildingRepository.findById(buildingId);
    }

    public Building getBuilding(UUID buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException(buildingId));
    }

    /**
     * Lists the registered buildings, optionally only those of one administrator.
     */
    public List<Building> listBuildings(UUID administratorUserId) {
        if (administratorUserId == null) {
            return buildingRepository.findAll();
        }
        return buildingRepository.findByAdministratorUserId(administratorUserId);
    }

    @Transactional
    public Building registerBuilding(Building building) {
        return buildingRepository.save(building);
    }
}
