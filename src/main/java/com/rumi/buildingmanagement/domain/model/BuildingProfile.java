package com.rumi.buildingmanagement.domain.model;

public record BuildingProfile(
        String name,
        String address,
        int floors,
        int constructionYear,
        int units
) {
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_ADDRESS_LENGTH = 200;
    public static final int MIN_CONSTRUCTION_YEAR = 1800;

    public BuildingProfile {
        requireText(name, MAX_NAME_LENGTH, "Building name");
        requireText(address, MAX_ADDRESS_LENGTH, "Building address");
        if (floors < 1) {
            throw new IllegalArgumentException("Building floors must be at least 1");
        }
        if (constructionYear < MIN_CONSTRUCTION_YEAR) {
            throw new IllegalArgumentException(
                    "Building construction year must be " + MIN_CONSTRUCTION_YEAR + " or later");
        }
        if (units < 1) {
            throw new IllegalArgumentException("Building units must be at least 1");
        }
    }

    private static void requireText(String value, int maxLength, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must have at most " + maxLength + " characters");
        }
    }
}
