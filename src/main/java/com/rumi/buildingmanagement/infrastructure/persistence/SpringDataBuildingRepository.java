package com.rumi.buildingmanagement.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataBuildingRepository extends JpaRepository<BuildingJpaEntity, UUID> {

    List<BuildingJpaEntity> findAllByOrderByNameAsc();

    List<BuildingJpaEntity> findByAdministratorUserIdOrderByNameAsc(UUID administratorUserId);
}
