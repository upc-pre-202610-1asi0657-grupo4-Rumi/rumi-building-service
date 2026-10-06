package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.repository.SensorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSensorRepository implements SensorRepository {

    private final SpringDataSensorRepository springDataRepository;
    private final SpringDataBuildingRepository springDataBuildingRepository;

    public JpaSensorRepository(
            SpringDataSensorRepository springDataRepository,
            SpringDataBuildingRepository springDataBuildingRepository
    ) {
        this.springDataRepository = springDataRepository;
        this.springDataBuildingRepository = springDataBuildingRepository;
    }

    @Override
    public Optional<Sensor> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Sensor> findByBuildingId(UUID buildingId) {
        return springDataRepository.findByBuilding_IdOrderByZoneAscIdAsc(buildingId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Sensor save(Sensor sensor) {
        return toDomain(springDataRepository.save(toEntity(sensor)));
    }

    private SensorJpaEntity toEntity(Sensor sensor) {
        return new SensorJpaEntity(
                sensor.getId(),
                springDataBuildingRepository.getReferenceById(sensor.getBuildingId()),
                sensor.getZone(),
                sensor.getType(),
                sensor.getStatus()
        );
    }

    private Sensor toDomain(SensorJpaEntity entity) {
        return new Sensor(
                entity.getId(),
                entity.getBuilding().getId(),
                entity.getZone(),
                entity.getType(),
                entity.getStatus()
        );
    }
}
