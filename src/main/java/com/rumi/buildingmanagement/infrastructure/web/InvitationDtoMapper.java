package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import com.rumi.buildingmanagement.infrastructure.web.dto.InvitationResponseDto;
import org.springframework.stereotype.Component;

@Component
public class InvitationDtoMapper {

    public InvitationResponseDto toResponse(ResidentInvitation invitation) {
        return new InvitationResponseDto(
                invitation.getId(),
                invitation.getBuildingId(),
                invitation.getCode(),
                invitation.getStatus(),
                invitation.getCreatedAt()
        );
    }
}
