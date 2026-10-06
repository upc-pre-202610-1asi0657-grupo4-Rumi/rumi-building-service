package com.rumi.buildingmanagement.application;

import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import com.rumi.buildingmanagement.domain.repository.ResidentInvitationRepository;

import java.util.ArrayList;
import java.util.List;

final class InMemoryResidentInvitationRepository implements ResidentInvitationRepository {

    private final List<ResidentInvitation> invitations = new ArrayList<>();

    @Override
    public boolean existsByCode(String code) {
        return invitations.stream().anyMatch(invitation -> invitation.getCode().equals(code));
    }

    @Override
    public ResidentInvitation save(ResidentInvitation invitation) {
        invitations.add(invitation);
        return invitation;
    }

    List<ResidentInvitation> saved() {
        return List.copyOf(invitations);
    }
}
