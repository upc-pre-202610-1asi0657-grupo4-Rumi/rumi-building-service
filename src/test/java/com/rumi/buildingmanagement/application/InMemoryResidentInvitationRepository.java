package com.rumi.buildingmanagement.application;

import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import com.rumi.buildingmanagement.domain.repository.ResidentInvitationRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

final class InMemoryResidentInvitationRepository implements ResidentInvitationRepository {

    private final List<ResidentInvitation> invitations = new ArrayList<>();

    @Override
    public List<ResidentInvitation> findByBuildingId(UUID buildingId) {
        return invitations.stream()
                .filter(invitation -> invitation.getBuildingId().equals(buildingId))
                .sorted(Comparator.comparing(ResidentInvitation::getCreatedAt).reversed())
                .toList();
    }

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
