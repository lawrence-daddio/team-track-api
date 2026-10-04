# TeamTrack API

A Spring Boot REST API for managing teams, projects, and tasks.

> **Work in progress.** Planned next: replace basic auth with JWT-based authentication and build a frontend.

## Features

- Employees, teams, and team memberships (with roles such as `LEAD` and `MEMBER`)
- Projects owned by teams, and tasks assigned to employees within projects
- Comments on tasks
- DTO mapping with MapStruct and request validation
- Controller, entity-mapping, and DTO-mapper tests

## Tech stack

- Java 25
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security)
- H2 in-memory database (with the H2 console enabled)
- Lombok and MapStruct
- Maven

## Getting started

**Prerequisites:** JDK 25

```bash
git clone https://github.com/lawrence-daddio/team-track-api.git
cd team-track-api
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8080` and is seeded with sample data from
`src/main/resources/team_track.sql`.

Run the tests:

```bash
./mvnw test
```

## API overview

| Resource           | Base path           |
|--------------------|---------------------|
| Employees          | `/employees`        |
| Teams              | `/teams`            |
| Team memberships   | `/team-memberships` |
| Projects           | `/projects`         |
| Tasks              | `/tasks`            |
| Comments           | `/comments`         |

Each resource supports the usual `GET`, `POST`, `PUT`, and `DELETE` operations, plus lookups such as
`/comments/task/{taskId}` and `/comments/employee/{employeeId}`.

## Configuration

Settings live in `src/main/resources/application.properties`. The database is in-memory, so data resets on every restart.
The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`).

## Security status

The app is configured for HTTP basic auth, but all endpoints are currently open (`permitAll`) for development.
The credentials in `application.properties` are dev-only placeholders. JWT authentication is planned.

## Roadmap

- [ ] JWT authentication (replacing basic auth)
- [ ] Lock down endpoints and add role-based access
- [ ] Frontend
- [ ] Persistent database
