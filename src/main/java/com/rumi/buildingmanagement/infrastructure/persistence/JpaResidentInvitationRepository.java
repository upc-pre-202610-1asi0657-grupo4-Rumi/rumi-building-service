package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import com.rumi.buildingmanagement.domain.repository.ResidentInvitationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaResidentInvitationRepository implements ResidentInvitationRepository {

    private final SpringDataResidentInvitationRepository springDataRepository;
    private final SpringDataBuildingRepository springDataBuildingRepository;

    public JpaResidentInvitationRepository(
            SpringDataResidentInvitationRepository springDataRepository,
            SpringDataBuildingRepository springDataBuildingRepository
    ) {
        this.springDataRepository = springDataRepository;
        this.springDataBuildingRepository = springDataBuildingRepository;
    }

    @Override
    public boolean existsByCode(String code) {
        return springDataRepository.existsByCode(code);
    }

    @Override
    public ResidentInvitation save(ResidentInvitation invitation) {
        return toDomain(springDataRepository.save(toEntity(invitation)));
    }

    private ResidentInvitationJpaEntity toEntity(ResidentInvitation invitation) {
        return new ResidentInvitationJpaEntity(
                invitation.getId(),
                springDataBuildingRepository.getReferenceById(invitation.getBuildingId()),
                invitation.getCode(),
                invitation.getStatus(),
                invitation.getCreatedAt()
        );
    }

    private ResidentInvitation toDomain(ResidentInvitationJpaEntity entity) {
        return new ResidentInvitation(
                entity.getId(),
                entity.getBuilding().getId(),
                entity.getCode(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
