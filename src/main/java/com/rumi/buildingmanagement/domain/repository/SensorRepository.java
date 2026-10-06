package com.rumi.buildingmanagement.domain.repository;

import com.rumi.buildingmanagement.domain.model.Sensor;

import java.util.List;
import java.util.UUID;

public interface SensorRepository {

    List<Sensor> findByBuildingId(UUID buildingId);

    Sensor save(Sensor sensor);
}
