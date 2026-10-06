package com.rumi.buildingmanagement.application;

import com.rumi.buildingmanagement.domain.model.Sensor;
import com.rumi.buildingmanagement.domain.repository.SensorRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class InMemorySensorRepository implements SensorRepository {

    private final Map<UUID, Sensor> sensors = new LinkedHashMap<>();

    @Override
    public Sensor save(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        return sensor;
    }

    List<Sensor> saved() {
        return List.copyOf(sensors.values());
    }
}
