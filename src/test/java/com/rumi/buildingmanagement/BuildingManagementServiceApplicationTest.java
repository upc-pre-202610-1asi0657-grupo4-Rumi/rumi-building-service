package com.rumi.buildingmanagement;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class BuildingManagementServiceApplicationTest {

    @Autowired
    private BuildingApplicationService buildingService;

    @Test
    void contextLoadsWithTheDevProfileAndNoExternalInfrastructure() {
        assertThat(buildingService).isNotNull();
    }
}
