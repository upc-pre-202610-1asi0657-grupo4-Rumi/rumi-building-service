package com.rumi.buildingmanagement.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildingStepDefinitions {

    private final BuildingIntegrationTestConfiguration context;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ResponseEntity<String> response;
    private UUID buildingId;

    public BuildingStepDefinitions(BuildingIntegrationTestConfiguration context) {
        this.context = context;
    }

    @Before
    public void resetScenarioState() {
        response = null;
        buildingId = null;
    }

    @When("I register a building named {string} for administrator {string}")
    public void registerBuilding(String name, String administratorId) {
        postBuilding(name, UUID.fromString(administratorId));
    }

    @Given("I have registered a building named {string}")
    public void haveRegisteredBuilding(String name) {
        postBuilding(name, UUID.randomUUID());
        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @When("I generate a resident invitation for that building")
    public void generateInvitation() {
        response = post("/api/v1/invitations", "{\"buildingId\":\"" + buildingId + "\"}");
    }

    @When("I register an {string} sensor in zone {string}")
    public void registerSensor(String type, String zone) {
        response = post("/api/v1/buildings/" + buildingId + "/sensors",
                "{\"zone\":\"" + zone + "\",\"type\":\"" + type + "\"}");
    }

    @Then("the building response has status {int} and state {string}")
    public void checkBuildingResponse(int status, String state) throws Exception {
        assertThat(response.getStatusCode().value()).isEqualTo(status);
        JsonNode body = objectMapper.readTree(response.getBody());
        buildingId = UUID.fromString(body.get("id").asText());
        assertThat(body.get("status").asText()).isEqualTo(state);
    }

    @Then("the registered building can be retrieved from the service")
    public void retrieveBuilding() {
        response = context.http.getForEntity(context.url("/api/v1/buildings/" + buildingId), String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Then("the invitation response has status {int} and state {string}")
    public void checkInvitationResponse(int status, String state) throws Exception {
        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(objectMapper.readTree(response.getBody()).get("status").asText()).isEqualTo(state);
    }

    @Then("the invitation is listed for that building")
    public void invitationIsListed() throws Exception {
        response = context.http.getForEntity(
                context.url("/api/v1/invitations?buildingId=" + buildingId), String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(objectMapper.readTree(response.getBody())).hasSize(1);
    }

    @Then("the sensor response has status {int} and state {string}")
    public void checkSensorResponse(int status, String state) throws Exception {
        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(objectMapper.readTree(response.getBody()).get("status").asText()).isEqualTo(state);
    }

    @Then("the sensor is listed for that building")
    public void sensorIsListed() throws Exception {
        response = context.http.getForEntity(
                context.url("/api/v1/buildings/" + buildingId + "/sensors"), String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(objectMapper.readTree(response.getBody())).hasSize(1);
    }

    private void postBuilding(String name, UUID administratorId) {
        response = post("/api/v1/buildings", "{\"name\":\"" + name
                + "\",\"address\":\"Av. Prueba 123\",\"floors\":8,\"constructionYear\":2018,"
                + "\"units\":24,\"administratorUserId\":\"" + administratorId + "\"}");
        if (response.getStatusCode().value() == 201) {
            try {
                buildingId = UUID.fromString(objectMapper.readTree(response.getBody()).get("id").asText());
            } catch (Exception exception) {
                throw new AssertionError("Could not read the created building id", exception);
            }
        }
    }

    private ResponseEntity<String> post(String path, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return context.http.exchange(context.url(path), HttpMethod.POST, new HttpEntity<>(json, headers), String.class);
    }
}
