package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingProfile;
import com.rumi.buildingmanagement.domain.repository.BuildingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaBuildingRepository implements BuildingRepository {

    private final SpringDataBuildingRepository springDataRepository;

    public JpaBuildingRepository(SpringDataBuildingRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Building> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Building> findAll() {
        return springDataRepository.findAllByOrderByNameAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Building> findByAdministratorUserId(UUID administratorUserId) {
        return springDataRepository.findByAdministratorUserIdOrderByNameAsc(administratorUserId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Building save(Building building) {
        BuildingJpaEntity savedEntity = springDataRepository.save(toEntity(building));
        return toDomain(savedEntity);
    }

    @Override
    public boolean existsById(UUID id) {
        return springDataRepository.existsById(id);
    }

    private BuildingJpaEntity toEntity(Building building) {
        BuildingProfile profile = building.getProfile();
        return new BuildingJpaEntity(
                building.getId(),
                profile.name(),
                profile.address(),
                profile.floors(),
                profile.constructionYear(),
                profile.units(),
                building.getStatus(),
                building.getAdministratorUserId()
        );
    }

    private Building toDomain(BuildingJpaEntity entity) {
        return new Building(
                entity.getId(),
                new BuildingProfile(
                        entity.getName(),
                        entity.getAddress(),
                        entity.getFloors(),
                        entity.getConstructionYear(),
                        entity.getUnits()
                ),
                entity.getStatus(),
                entity.getAdministratorUserId()
        );
    }
}
