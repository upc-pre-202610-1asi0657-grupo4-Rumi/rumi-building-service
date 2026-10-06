package com.rumi.buildingmanagement.infrastructure.web;

/**
 * Example payloads shown in the OpenAPI documentation.
 */
final class OpenApiExamples {

    static final String PROBLEM_JSON = "application/problem+json";

    static final String BUILDING_REQUEST = """
            {
              "name": "Torre Miraflores",
              "address": "Av. Larco 1234, Miraflores",
              "floors": 12,
              "constructionYear": 2015,
              "units": 48,
              "administratorUserId": "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"
            }""";

    static final String BUILDING_RESPONSE = """
            {
              "id": "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d",
              "name": "Torre Miraflores",
              "address": "Av. Larco 1234, Miraflores",
              "floors": 12,
              "constructionYear": 2015,
              "units": 48,
              "status": "PENDING_SENSORS",
              "administratorUserId": "c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f"
            }""";

    static final String BUILDING_VALIDATION_ERROR = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Invalid request content.",
              "instance": "/api/v1/buildings",
              "errors": {
                "floors": "must be greater than or equal to 1",
                "name": "must not be blank"
              }
            }""";

    static final String BUILDING_MALFORMED_BODY = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Failed to read request",
              "instance": "/api/v1/buildings"
            }""";

    private OpenApiExamples() {
    }
}
