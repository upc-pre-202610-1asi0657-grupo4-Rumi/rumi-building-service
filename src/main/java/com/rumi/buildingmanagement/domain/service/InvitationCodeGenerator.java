package com.rumi.buildingmanagement.domain.service;

/**
 * Produces the random codes that residents use to join a building.
 */
public interface InvitationCodeGenerator {

    String generate();
}
