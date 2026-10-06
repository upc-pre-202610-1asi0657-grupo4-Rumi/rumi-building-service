package com.rumi.buildingmanagement.application;

import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.model.SensorStatus;
import com.rumi.buildingmanagement.domain.model.SensorType;
import com.rumi.buildingmanagement.domain.repository.BuildingRepository;
import com.rumi.buildingmanagement.domain.repository.ResidentInvitationRepository;
import com.rumi.buildingmanagement.domain.repository.SensorRepository;
import com.rumi.buildingmanagement.domain.service.InvitationCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BuildingApplicationService {

    private static final int MAX_CODE_ATTEMPTS = 5;

    private final BuildingRepository buildingRepository;
    private final SensorRepository sensorRepository;
    private final ResidentInvitationRepository invitationRepository;
    private final InvitationCodeGenerator invitationCodeGenerator;

    public BuildingApplicationService(
            BuildingRepository buildingRepository,
            SensorRepository sensorRepository,
            ResidentInvitationRepository invitationRepository,
            InvitationCodeGenerator invitationCodeGenerator
    ) {
        this.buildingRepository = buildingRepository;
        this.sensorRepository = sensorRepository;
        this.invitationRepository = invitationRepository;
        this.invitationCodeGenerator = invitationCodeGenerator;
    }

    public Optional<Building> findBuildingById(UUID buildingId) {
        return buildingRepository.findById(buildingId);
    }

    public Building getBuilding(UUID buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BuildingNotFoundException(buildingId));
    }

    /**
     * Lists the registered buildings, optionally only those of one administrator.
     */
    public List<Building> listBuildings(UUID administratorUserId) {
        if (administratorUserId == null) {
            return buildingRepository.findAll();
        }
        return buildingRepository.findByAdministratorUserId(administratorUserId);
    }

    @Transactional
    public Building registerBuilding(Building building) {
        return buildingRepository.save(building);
    }

    @Transactional
    public Sensor registerSensor(UUID buildingId, String zone, SensorType type) {
        Building building = getBuilding(buildingId);
        return sensorRepository.save(building.registerSensor(zone, type));
    }

    public List<Sensor> listSensors(UUID buildingId) {
        Building building = getBuilding(buildingId);
        return sensorRepository.findByBuildingId(building.getId());
    }

    /**
     * Changes the status of a sensor. The first sensor that becomes active activates its building.
     */
    @Transactional
    public Sensor updateSensorStatus(UUID sensorId, SensorStatus status) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
        sensor.changeStatus(status);
        Sensor savedSensor = sensorRepository.save(sensor);

        if (savedSensor.isActive()) {
            Building building = getBuilding(savedSensor.getBuildingId());
            if (building.isPendingSensors()) {
                building.activate();
                buildingRepository.save(building);
            }
        }
        return savedSensor;
    }

    /**
     * Generates an invitation with a code that no other invitation uses.
     */
    @Transactional
    public ResidentInvitation inviteResident(UUID buildingId) {
        Building building = getBuilding(buildingId);
        ResidentInvitation invitation = building.generateInvitation(newUniqueCode(), Instant.now());
        return invitationRepository.save(invitation);
    }

    public List<ResidentInvitation> listInvitations(UUID buildingId) {
        Building building = getBuilding(buildingId);
        return invitationRepository.findByBuildingId(building.getId());
    }

    private String newUniqueCode() {
        for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
            String code = invitationCodeGenerator.generate();
            if (!invitationRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Could not generate a unique invitation code");
    }
}
