package com.rumi.buildingmanagement.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataResidentInvitationRepository extends JpaRepository<ResidentInvitationJpaEntity, UUID> {

    List<ResidentInvitationJpaEntity> findByBuilding_IdOrderByCreatedAtDescIdAsc(UUID buildingId);

    boolean existsByCode(String code);
}
