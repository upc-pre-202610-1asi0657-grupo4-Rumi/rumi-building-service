package com.rumi.buildingmanagement.application;

import com.rumi.buildingmanagement.BuildingFixtures;
import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingStatus;
import com.rumi.buildingmanagement.domain.model.InvitationStatus;
import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.model.SensorStatus;
import com.rumi.buildingmanagement.domain.model.SensorType;
import com.rumi.buildingmanagement.domain.repository.BuildingRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class BuildingApplicationServiceTest {

    private final BuildingRepository repository = new InMemoryBuildingRepository();
    private final InMemorySensorRepository sensorRepository = new InMemorySensorRepository();
    private final InMemoryResidentInvitationRepository invitationRepository =
            new InMemoryResidentInvitationRepository();
    private final Deque<String> codes = new ArrayDeque<>();
    private final BuildingApplicationService service = new BuildingApplicationService(
            repository, sensorRepository, invitationRepository, codes::removeFirst);

    @Test
    void registersAndFindsABuildingThroughTheDomainRepository() {
        Building building = BuildingFixtures.pendingBuilding();

        service.registerBuilding(building);

        assertThat(service.findBuildingById(building.getId())).containsSame(building);
    }

    @Test
    void listsEveryRegisteredBuilding() {
        Building first = service.registerBuilding(BuildingFixtures.pendingBuilding());
        Building second = service.registerBuilding(BuildingFixtures.pendingBuilding());

        assertThat(service.listBuildings(null)).containsExactly(first, second);
    }

    @Test
    void listsOnlyTheBuildingsOfAnAdministrator() {
        UUID otherAdministrator = UUID.randomUUID();
        service.registerBuilding(BuildingFixtures.pendingBuilding());
        Building owned = service.registerBuilding(
                Building.register(UUID.randomUUID(), BuildingFixtures.profile(), otherAdministrator));

        assertThat(service.listBuildings(otherAdministrator)).containsExactly(owned);
    }

    @Test
    void getsARegisteredBuilding() {
        Building building = service.registerBuilding(BuildingFixtures.pendingBuilding());

        assertThat(service.getBuilding(building.getId())).isSameAs(building);
    }

    @Test
    void failsToGetAnUnknownBuilding() {
        UUID unknownId = UUID.randomUUID();

        assertThatThrownBy(() -> service.getBuilding(unknownId))
                .isInstanceOf(BuildingNotFoundException.class)
                .hasMessage("Building " + unknownId + " was not found");
    }

    @Test
    void registersAPendingSensorInABuilding() {
        Building building = service.registerBuilding(BuildingFixtures.pendingBuilding());

        Sensor sensor = service.registerSensor(building.getId(), "FLOOR-3-NORTH", SensorType.ACCELEROMETER);

        assertThat(sensor.getId()).isNotNull();
        assertThat(sensor.getBuildingId()).isEqualTo(building.getId());
        assertThat(sensor.getZone()).isEqualTo("FLOOR-3-NORTH");
        assertThat(sensor.getType()).isEqualTo(SensorType.ACCELEROMETER);
        assertThat(sensor.getStatus()).isEqualTo(SensorStatus.PENDING);
        assertThat(sensorRepository.saved()).containsExactly(sensor);
    }

    @Test
    void failsToRegisterASensorInAnUnknownBuilding() {
        assertThatThrownBy(() -> service.registerSensor(UUID.randomUUID(), "FLOOR-3-NORTH", SensorType.ACCELEROMETER))
                .isInstanceOf(BuildingNotFoundException.class);
        assertThat(sensorRepository.saved()).isEmpty();
    }

    @Test
    void listsOnlyTheSensorsOfTheRequestedBuilding() {
        Building building = service.registerBuilding(BuildingFixtures.pendingBuilding());
        Building other = service.registerBuilding(BuildingFixtures.pendingBuilding());
        Sensor sensor = service.registerSensor(building.getId(), "FLOOR-3-NORTH", SensorType.ACCELEROMETER);
        service.registerSensor(other.getId(), "ROOF-SOUTH", SensorType.INCLINOMETER);

        assertThat(service.listSensors(building.getId())).containsExactly(sensor);
    }

    @Test
    void failsToListTheSensorsOfAnUnknownBuilding() {
        assertThatThrownBy(() -> service.listSensors(UUID.randomUUID()))
                .isInstanceOf(BuildingNotFoundException.class);
    }

    @Test
    void activatingTheFirstSensorActivatesItsBuilding() {
        Building building = service.registerBuilding(BuildingFixtures.pendingBuilding());
        Sensor sensor = service.registerSensor(building.getId(), "FLOOR-3-NORTH", SensorType.ACCELEROMETER);

        Sensor updated = service.updateSensorStatus(sensor.getId(), SensorStatus.ACTIVE);

        assertThat(updated.getStatus()).isEqualTo(SensorStatus.ACTIVE);
        assertThat(service.getBuilding(building.getId()).getStatus()).isEqualTo(BuildingStatus.ACTIVE);
    }

    @Test
    void aSensorThatDoesNotBecomeActiveLeavesTheBuildingPending() {
        Building building = service.registerBuilding(BuildingFixtures.pendingBuilding());
        Sensor sensor = service.registerSensor(building.getId(), "FLOOR-3-NORTH", SensorType.ACCELEROMETER);

        Sensor updated = service.updateSensorStatus(sensor.getId(), SensorStatus.INACTIVE);

        assertThat(updated.getStatus()).isEqualTo(SensorStatus.INACTIVE);
        assertThat(service.getBuilding(building.getId()).getStatus()).isEqualTo(BuildingStatus.PENDING_SENSORS);
    }

    @Test
    void anActiveBuildingStaysActiveWhenItsSensorIsDeactivated() {
        Building building = service.registerBuilding(BuildingFixtures.pendingBuilding());
        Sensor sensor = service.registerSensor(building.getId(), "FLOOR-3-NORTH", SensorType.ACCELEROMETER);
        service.updateSensorStatus(sensor.getId(), SensorStatus.ACTIVE);

        service.updateSensorStatus(sensor.getId(), SensorStatus.INACTIVE);

        assertThat(service.getBuilding(building.getId()).getStatus()).isEqualTo(BuildingStatus.ACTIVE);
    }

    @Test
    void failsToUpdateTheStatusOfAnUnknownSensor() {
        UUID unknownId = UUID.randomUUID();

        assertThatThrownBy(() -> service.updateSensorStatus(unknownId, SensorStatus.ACTIVE))
                .isInstanceOf(SensorNotFoundException.class)
                .hasMessage("Sensor " + unknownId + " was not found");
    }

    @Test
    void invitesAResidentWithAPendingInvitation() {
        Building building = service.registerBuilding(BuildingFixtures.pendingBuilding());
        codes.add("RUMI-7K2M9QXD");

        ResidentInvitation invitation = service.inviteResident(building.getId());

        assertThat(invitation.getId()).isNotNull();
        assertThat(invitation.getBuildingId()).isEqualTo(building.getId());
        assertThat(invitation.getCode()).isEqualTo("RUMI-7K2M9QXD");
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(invitation.getCreatedAt()).isCloseTo(Instant.now(), within(5, ChronoUnit.SECONDS));
        assertThat(invitationRepository.saved()).containsExactly(invitation);
    }

    @Test
    void generatesAnotherCodeWhenTheFirstOneIsAlreadyUsed() {
        Building building = service.registerBuilding(BuildingFixtures.pendingBuilding());
        codes.addAll(List.of("RUMI-AAAAAAAA", "RUMI-AAAAAAAA", "RUMI-BBBBBBBB"));
        service.inviteResident(building.getId());

        ResidentInvitation second = service.inviteResident(building.getId());

        assertThat(second.getCode()).isEqualTo("RUMI-BBBBBBBB");
    }

    @Test
    void failsToInviteAResidentToAnUnknownBuilding() {
        assertThatThrownBy(() -> service.inviteResident(UUID.randomUUID()))
                .isInstanceOf(BuildingNotFoundException.class);
        assertThat(invitationRepository.saved()).isEmpty();
    }
}
