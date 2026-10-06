package com.rumi.buildingmanagement.infrastructure.web;

/**
 * Example payloads shown in the OpenAPI documentation.
 */
final class OpenApiExamples {

    static final String PROBLEM_JSON = "application/problem+json";

    static final String BUILDING_REQUEST = """
            {
              "name": "Rumi Tower",
              "address": "Av. Arequipa 1234, Lince, Lima"
            }""";

    static final String BUILDING_RESPONSE = """
            {
              "id": "3f2c8a10-5d7b-4e9a-b1c2-0a1b2c3d4e5f",
              "name": "Rumi Tower",
              "address": "Av. Arequipa 1234, Lince, Lima"
            }""";

    static final String BUILDING_VALIDATION_ERROR = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Invalid request content.",
              "instance": "/api/v1/buildings",
              "errors": {
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
