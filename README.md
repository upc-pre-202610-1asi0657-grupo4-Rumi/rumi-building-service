# rumi-building-service

Building Management service of **Rumi**, the structural monitoring platform by Kuntur Labs.

| | |
|---|---|
| Bounded context | Building Management |
| Port | `8081` |
| Database | `buildingsdb` (PostgreSQL) |
| Gateway routes | `/api/v1/buildings/**`, `/api/v1/sensors/**`, `/api/v1/invitations/**` |
| Base package | `com.rumi.buildingmanagement` |

## Purpose

Owns the buildings registered in Rumi, the IoT sensors installed in their zones and the
invitation codes that let residents join a building.

## Origin

Extracted from the modular monolith
[`rumi-backend`](https://github.com/upc-pre-202610-1asi0657-grupo4-Rumi/rumi-backend) at commit
`rumi-backend@3ab07ec` (branch `develop`), with the history of this context preserved.
The state of the monolith before the decomposition is the tag
[`monolith-baseline`](https://github.com/upc-pre-202610-1asi0657-grupo4-Rumi/rumi-backend/tree/monolith-baseline).

## Endpoints

| Verb | Path | Description | User story | Status |
|---|---|---|---|---|
| POST | `/api/v1/buildings` | Register a building | US04 | implemented |
| GET | `/api/v1/buildings` | List buildings (optional `administratorUserId` filter) | US04 | implemented |
| GET | `/api/v1/buildings/{buildingId}` | Get a building | US04 | implemented |
| POST | `/api/v1/buildings/{buildingId}/sensors` | Register a sensor in a building | US08 | implemented |
| GET | `/api/v1/buildings/{buildingId}/sensors` | List the sensors of a building | US08 | implemented |
| PATCH | `/api/v1/sensors/{sensorId}/status` | Update the status of a sensor | US08 | implemented |
| POST | `/api/v1/invitations` | Invite a resident to a building | US06 | implemented |
| GET | `/api/v1/invitations?buildingId=` | List the invitations of a building | US06 | implemented |

Implemented functional endpoints: 8 of the 8 planned for this service.

Rules:

- A building is created as `PENDING_SENSORS`. It becomes `ACTIVE` when its first sensor becomes `ACTIVE`.
- A sensor is created as `PENDING`. `PATCH /api/v1/sensors/{sensorId}/status` accepts `PENDING`, `ACTIVE`
  or `INACTIVE`; it activates sensors by hand until readings arrive by messaging.
- An invitation is created as `PENDING` with a unique random code such as `RUMI-7K2M9QXD`.
- `administratorUserId` is sent in the body of `POST /api/v1/buildings` until the IAM service exists.

Errors are RFC 7807 problem details (`application/problem+json`): `400` for invalid input
(with an `errors` object, one message per invalid field) and `404` for an unknown building or sensor.

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/api/v1/buildings",
  "errors": { "name": "must not be blank" }
}
```

## API documentation

- Swagger UI: <http://localhost:8081/swagger-ui.html>
- OpenAPI spec: <http://localhost:8081/v3/api-docs>
- Exported spec: [`docs/openapi.json`](docs/openapi.json)

Start the service (the `dev` profile is enough) and open Swagger UI in the browser. Every operation
has a description and an example for its request, its responses and its errors.

## Run

The repository includes the Maven wrapper, so only JDK 21 is required. Use `mvnw.cmd` on Windows.

### Development profile (no external infrastructure)

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

or, with the packaged jar:

```sh
./mvnw package
java -jar target/rumi-building-service-0.1.0.jar --spring.profiles.active=dev
```

The `dev` profile uses an in-memory H2 database that is created on startup and lost on shutdown.
It loads sample data with fixed identifiers, shared with the sample data of the other Rumi services:

| Resource | Id | Data |
|---|---|---|
| Building | `7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d` | Torre Miraflores, Av. Larco 1234, Miraflores, `ACTIVE` |
| Sensor | `5d1c2f0e-8f4a-4a53-9a7e-0f3b1c9d7a11` | zone `FLOOR-3-NORTH`, `ACCELEROMETER`, `ACTIVE` |

```sh
curl http://localhost:8081/api/v1/buildings/7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d
```

### Default profile (PostgreSQL)

Requirements: JDK 21 and PostgreSQL. Create the database and its tables with
[`docs/schema.sql`](docs/schema.sql) (Hibernate runs with `ddl-auto=validate`, the tables must exist).

```sh
./mvnw spring-boot:run
```

| Variable | Default |
|---|---|
| `SERVER_PORT` | `8081` |
| `BUILDING_DB_URL` | `jdbc:postgresql://localhost:5432/buildingsdb` |
| `BUILDING_DB_USERNAME` | `postgres` |
| `BUILDING_DB_PASSWORD` | `postgres` |

## Test

```sh
./mvnw test
```

The tests need neither PostgreSQL nor a message broker: persistence tests run on in-memory H2.

## Structure

```
com.rumi.buildingmanagement
├── application                  use cases
├── domain
│   ├── model                    Building, BuildingProfile, Sensor, ResidentInvitation and their enums
│   ├── repository               repository ports
│   └── service                  InvitationCodeGenerator port
└── infrastructure
    ├── invitation               random invitation code generator
    ├── persistence              JPA adapters
    ├── seed                     sample data of the dev profile
    └── web                      REST controllers, DTOs, error handling, OpenAPI configuration
```
