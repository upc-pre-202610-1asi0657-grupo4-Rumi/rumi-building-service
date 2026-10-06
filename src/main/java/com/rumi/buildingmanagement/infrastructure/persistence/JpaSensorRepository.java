package com.rumi.buildingmanagement.infrastructure.persistence;

import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.repository.SensorRepository;
import org.springframework.stereotype.Repository;

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
