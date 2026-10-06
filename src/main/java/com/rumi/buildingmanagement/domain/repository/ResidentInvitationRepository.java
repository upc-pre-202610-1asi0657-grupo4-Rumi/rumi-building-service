package com.rumi.buildingmanagement.domain.repository;

import com.rumi.buildingmanagement.domain.model.ResidentInvitation;

public interface ResidentInvitationRepository {

    boolean existsByCode(String code);

    ResidentInvitation save(ResidentInvitation invitation);
}
