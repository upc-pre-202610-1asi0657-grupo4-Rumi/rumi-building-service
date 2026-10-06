package com.rumi.buildingmanagement.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataSensorRepository extends JpaRepository<SensorJpaEntity, UUID> {

    List<SensorJpaEntity> findByBuilding_IdOrderByZoneAscIdAsc(UUID buildingId);
}
