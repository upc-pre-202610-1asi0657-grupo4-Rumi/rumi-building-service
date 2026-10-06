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

Owns the buildings registered in Rumi. Sensors and resident invitations belong to this
context as well and are planned for later sprints.

## Origin

Extracted from the modular monolith
[`rumi-backend`](https://github.com/upc-pre-202610-1asi0657-grupo4-Rumi/rumi-backend) at commit
`rumi-backend@3ab07ec` (branch `develop`), with the history of this context preserved.
The state of the monolith before the decomposition is the tag
[`monolith-baseline`](https://github.com/upc-pre-202610-1asi0657-grupo4-Rumi/rumi-backend/tree/monolith-baseline).

## Endpoints

| Verb | Path | Description | Request | Response | User story | Status |
|---|---|---|---|---|---|---|
| POST | `/api/v1/buildings` | Register a building | `BuildingRequest` | `201` `BuildingResponse` | US04 | implemented |

Implemented functional endpoints: 1.

## API documentation

- Swagger UI: <http://localhost:8081/swagger-ui.html>
- OpenAPI spec: <http://localhost:8081/v3/api-docs>
- Exported spec: [`docs/openapi.json`](docs/openapi.json)

## Run

Requirements: JDK 21, Maven, PostgreSQL.

```sql
CREATE DATABASE buildingsdb;

-- inside buildingsdb (hibernate.ddl-auto is "validate", the table must exist)
CREATE TABLE buildings (
    id      uuid PRIMARY KEY,
    name    varchar(255) NOT NULL,
    address varchar(255) NOT NULL
);
```

```sh
mvn spring-boot:run
```

| Variable | Default |
|---|---|
| `SERVER_PORT` | `8081` |
| `BUILDING_DB_URL` | `jdbc:postgresql://localhost:5432/buildingsdb` |
| `BUILDING_DB_USERNAME` | `postgres` |
| `BUILDING_DB_PASSWORD` | `postgres` |

## Test

```sh
mvn test
```

The tests need neither a database nor a message broker.

## Structure

```
com.rumi.buildingmanagement
├── application                  use cases
├── domain                       model and repository port
└── infrastructure
    ├── persistence              JPA adapter
    └── web                      REST controller, DTOs, OpenAPI configuration
```
