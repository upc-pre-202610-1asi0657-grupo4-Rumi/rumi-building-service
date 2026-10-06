package com.rumi.buildingmanagement.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataResidentInvitationRepository extends JpaRepository<ResidentInvitationJpaEntity, UUID> {

    boolean existsByCode(String code);
}
