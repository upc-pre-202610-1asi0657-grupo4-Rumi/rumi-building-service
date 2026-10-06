package com.rumi.buildingmanagement.domain.repository;

import com.rumi.buildingmanagement.domain.model.ResidentInvitation;

import java.util.List;
import java.util.UUID;

public interface ResidentInvitationRepository {

    List<ResidentInvitation> findByBuildingId(UUID buildingId);

    boolean existsByCode(String code);

    ResidentInvitation save(ResidentInvitation invitation);
}
