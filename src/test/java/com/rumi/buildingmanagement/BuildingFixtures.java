package com.rumi.buildingmanagement;

import com.rumi.buildingmanagement.domain.model.Building;
import com.rumi.buildingmanagement.domain.model.BuildingProfile;

import java.util.UUID;

/**
 * Sample domain objects shared by the tests.
 */
public final class BuildingFixtures {

    public static final UUID ADMINISTRATOR_ID = UUID.fromString("c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f");

    public static final String BUILDING_JSON = """
            {
              "name": "Torre Miraflores",
              "address": "Av. Larco 1234, Miraflores",
              "floors": 12,
              "constructionYear": 2015,
              "units": 48,
              "administratorUserId": "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"
            }""";

    private BuildingFixtures() {
    }

    public static BuildingProfile profile() {
        return new BuildingProfile("Torre Miraflores", "Av. Larco 1234, Miraflores", 12, 2015, 48);
    }

    public static Building pendingBuilding() {
        return Building.register(UUID.randomUUID(), profile(), ADMINISTRATOR_ID);
    }
}
