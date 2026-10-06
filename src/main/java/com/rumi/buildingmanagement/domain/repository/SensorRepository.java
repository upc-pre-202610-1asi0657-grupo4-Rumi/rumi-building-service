package com.rumi.buildingmanagement.domain.repository;

import com.rumi.buildingmanagement.domain.model.Sensor;

public interface SensorRepository {

    Sensor save(Sensor sensor);
}
