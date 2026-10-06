package com.rumi.buildingmanagement.infrastructure.web;

import com.rumi.buildingmanagement.application.BuildingApplicationService;
import com.rumi.buildingmanagement.application.BuildingNotFoundException;
import com.rumi.buildingmanagement.domain.model.ResidentInvitation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvitationController.class)
@Import(InvitationDtoMapper.class)
class InvitationControllerTest {

    private static final UUID BUILDING_ID = UUID.fromString("7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d");
    private static final String INVITATION_JSON = "{\"buildingId\":\"" + BUILDING_ID + "\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuildingApplicationService buildingService;

    @Test
    void createsAnInvitationAndReturnsItWithStatusCreated() throws Exception {
        ResidentInvitation invitation = invitation();
        when(buildingService.inviteResident(BUILDING_ID)).thenReturn(invitation);

        mockMvc.perform(post("/api/v1/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVITATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(invitation.getId().toString()))
                .andExpect(jsonPath("$.buildingId").value(BUILDING_ID.toString()))
                .andExpect(jsonPath("$.code").value("RUMI-7K2M9QXD"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").value("2026-10-06T15:30:00Z"));
    }

    @Test
    void rejectsAMissingBuildingIdWithABadRequestProblemDetail() throws Exception {
        mockMvc.perform(post("/api/v1/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.buildingId").value("must not be null"));

        verifyNoInteractions(buildingService);
    }

    @Test
    void returnsNotFoundWhenTheInvitedBuildingDoesNotExist() throws Exception {
        when(buildingService.inviteResident(BUILDING_ID)).thenThrow(new BuildingNotFoundException(BUILDING_ID));

        mockMvc.perform(post("/api/v1/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVITATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Building " + BUILDING_ID + " was not found"));
    }

    private static ResidentInvitation invitation() {
        return ResidentInvitation.generate(
                UUID.randomUUID(), BUILDING_ID, "RUMI-7K2M9QXD", Instant.parse("2026-10-06T15:30:00Z"));
    }

    @Test
    void listsTheInvitationsOfABuilding() throws Exception {
        ResidentInvitation invitation = invitation();
        when(buildingService.listInvitations(BUILDING_ID)).thenReturn(List.of(invitation));

        mockMvc.perform(get("/api/v1/invitations").param("buildingId", BUILDING_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("RUMI-7K2M9QXD"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void rejectsAListRequestWithoutBuildingId() throws Exception {
        mockMvc.perform(get("/api/v1/invitations"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Required parameter 'buildingId' is not present."));

        verifyNoInteractions(buildingService);
    }

    @Test
    void rejectsAListRequestWithABuildingIdThatIsNotAUuid() throws Exception {
        mockMvc.perform(get("/api/v1/invitations").param("buildingId", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Failed to convert 'buildingId' with value: 'not-a-uuid'"));
    }

    @Test
    void returnsNotFoundWhenListingTheInvitationsOfAnUnknownBuilding() throws Exception {
        when(buildingService.listInvitations(BUILDING_ID)).thenThrow(new BuildingNotFoundException(BUILDING_ID));

        mockMvc.perform(get("/api/v1/invitations").param("buildingId", BUILDING_ID.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }
}
