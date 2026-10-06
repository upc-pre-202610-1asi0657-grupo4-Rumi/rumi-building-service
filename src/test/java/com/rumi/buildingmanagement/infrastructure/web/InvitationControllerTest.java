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
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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
}
