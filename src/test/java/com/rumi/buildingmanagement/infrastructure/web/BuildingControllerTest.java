package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.BuildingFixtures;
import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.application.BuildingNotFoundException;
import com.rumi.buildingmanagement.domain.model.Building;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuildingController.class)
@Import(BuildingDtoMapper.class)
class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuildingApplicationService buildingService;

    @Test
    void registersABuildingAndReturnsItWithStatusCreated() throws Exception {
        when(buildingService.registerBuilding(any(Building.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BuildingFixtures.BUILDING_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Torre Miraflores"))
                .andExpect(jsonPath("$.address").value("Av. Larco 1234, Miraflores"))
                .andExpect(jsonPath("$.floors").value(12))
                .andExpect(jsonPath("$.constructionYear").value(2015))
                .andExpect(jsonPath("$.units").value(48))
                .andExpect(jsonPath("$.status").value("PENDING_SENSORS"))
                .andExpect(jsonPath("$.administratorUserId").value("c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"));
    }

    @Test
    void rejectsABlankNameWithABadRequestProblemDetail() throws Exception {
        mockMvc.perform(post("/api/v1/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BuildingFixtures.BUILDING_JSON.replace("Torre Miraflores", " ")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.instance").value("/api/v1/buildings"))
                .andExpect(jsonPath("$.errors.name").value("must not be blank"));

        verifyNoInteractions(buildingService);
    }

    @Test
    void rejectsAMissingAddressWithABadRequestProblemDetail() throws Exception {
        mockMvc.perform(post("/api/v1/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Torre Miraflores\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.address").value("must not be blank"))
                .andExpect(jsonPath("$.errors.floors").value("must not be null"))
                .andExpect(jsonPath("$.errors.administratorUserId").value("must not be null"));
    }

    @Test
    void rejectsAMalformedBodyWithABadRequestProblemDetail() throws Exception {
        mockMvc.perform(post("/api/v1/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Failed to read request"));
    }

    @Test
    void listsTheRegisteredBuildings() throws Exception {
        Building building = BuildingFixtures.pendingBuilding();
        when(buildingService.listBuildings(null)).thenReturn(List.of(building));

        mockMvc.perform(get("/api/v1/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(building.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("Torre Miraflores"))
                .andExpect(jsonPath("$[0].status").value("PENDING_SENSORS"));
    }

    @Test
    void filtersTheBuildingsByAdministrator() throws Exception {
        when(buildingService.listBuildings(BuildingFixtures.ADMINISTRATOR_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/buildings")
                        .param("administratorUserId", BuildingFixtures.ADMINISTRATOR_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void rejectsAnAdministratorIdThatIsNotAUuid() throws Exception {
        mockMvc.perform(get("/api/v1/buildings").param("administratorUserId", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(
                        "Failed to convert 'administratorUserId' with value: 'not-a-uuid'"));
    }

    @Test
    void returnsABuildingByItsId() throws Exception {
        Building building = BuildingFixtures.pendingBuilding();
        when(buildingService.getBuilding(building.getId())).thenReturn(building);

        mockMvc.perform(get("/api/v1/buildings/{buildingId}", building.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(building.getId().toString()))
                .andExpect(jsonPath("$.address").value("Av. Larco 1234, Miraflores"));
    }

    @Test
    void returnsNotFoundForAnUnknownBuilding() throws Exception {
        UUID buildingId = UUID.randomUUID();
        when(buildingService.getBuilding(buildingId)).thenThrow(new BuildingNotFoundException(buildingId));

        mockMvc.perform(get("/api/v1/buildings/{buildingId}", buildingId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Building " + buildingId + " was not found"))
                .andExpect(jsonPath("$.instance").value("/api/v1/buildings/" + buildingId));
    }

    @Test
    void rejectsABuildingIdThatIsNotAUuid() throws Exception {
        mockMvc.perform(get("/api/v1/buildings/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Failed to convert 'buildingId' with value: 'not-a-uuid'"));
    }
}
