package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.BuildingFixtures;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.InvitationStatus;
import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import({JpaBuildingRepository.class, JpaResidentInvitationRepository.class})
class JpaResidentInvitationRepositoryTest {

    private static final Instant CREATED_AT = Instant.parse("2026-10-06T15:30:00Z");

    @Autowired
    private JpaBuildingRepository buildingRepository;

    @Autowired
    private JpaResidentInvitationRepository invitationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesAnInvitationLinkedToItsBuilding() {
        Building building = buildingRepository.save(BuildingFixtures.pendingBuilding());
        ResidentInvitation invitation = building.generateInvitation("RUMI-7K2M9QXD", CREATED_AT);

        invitationRepository.save(invitation);
        entityManager.flush();
        entityManager.clear();

        ResidentInvitationJpaEntity stored = entityManager.find(ResidentInvitationJpaEntity.class, invitation.getId());
        assertThat(stored.getBuilding().getId()).isEqualTo(building.getId());
        assertThat(stored.getCode()).isEqualTo("RUMI-7K2M9QXD");
        assertThat(stored.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(stored.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void knowsWhichCodesAreAlreadyUsed() {
        Building building = buildingRepository.save(BuildingFixtures.pendingBuilding());
        invitationRepository.save(building.generateInvitation("RUMI-7K2M9QXD", CREATED_AT));
        entityManager.flush();

        assertThat(invitationRepository.existsByCode("RUMI-7K2M9QXD")).isTrue();
        assertThat(invitationRepository.existsByCode("RUMI-ZZZZZZZZ")).isFalse();
    }

    @Test
    void findsTheInvitationsOfABuildingNewestFirst() {
        Building building = buildingRepository.save(BuildingFixtures.pendingBuilding());
        Building other = buildingRepository.save(BuildingFixtures.pendingBuilding());
        invitationRepository.save(building.generateInvitation("RUMI-OLDER222", CREATED_AT));
        invitationRepository.save(building.generateInvitation("RUMI-NEWER222", CREATED_AT.plusSeconds(60)));
        invitationRepository.save(other.generateInvitation("RUMI-OTHER222", CREATED_AT));
        entityManager.flush();
        entityManager.clear();

        assertThat(invitationRepository.findByBuildingId(building.getId()))
                .extracting(ResidentInvitation::getCode)
                .containsExactly("RUMI-NEWER222", "RUMI-OLDER222");
    }
}
