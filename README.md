# TeamTrack API

A Spring Boot REST API for managing teams, projects, and tasks.

> **Work in progress.** Planned next: role-based access control (RBAC) and a frontend.

## Features

- Employees, teams, and team memberships (with roles such as `LEAD` and `MEMBER`)
- Projects owned by teams, and tasks assigned to employees within projects
- Comments on tasks
- JWT authentication (login endpoint issues a signed bearer token)
- DTO mapping with MapStruct and request validation
- Controller, entity-mapping, and DTO-mapper tests

## Tech stack

- Java 25
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security, OAuth2 Resource Server)
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

## Authentication

All endpoints except `POST /auth/login` and the H2 console require a JWT bearer token. Employees are the users:
the login name is the employee's email, and the password is checked against `password_hash`.

```bash
# 1. Log in
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice.johnson@example.com","password":"password1"}'
# -> {"token":"eyJ...","expiresIn":3600000}      (expiresIn is in milliseconds; 401 on bad credentials)

# 2. Call the API with the token
curl http://localhost:8080/employees -H "Authorization: Bearer <token>"
```

How it works:

- `AuthController` authenticates the credentials through Spring Security's `AuthenticationManager`;
  `EmployeeUserDetailsService` loads the `Employee` (which implements `UserDetails`) by email.
- `JwtService` signs tokens (HS256) with Spring Security's `JwtEncoder`; the subject is the employee's email.
- `SecurityConfig` runs as an OAuth2 resource server, so `JwtDecoder` validates the signature and expiry on each
  request. The app is stateless (no sessions).
- `JwtConfig` builds the key, encoder and decoder from the properties below.

> **Seed logins (dev only):** the seeded employees `alice.johnson@example.com`, `bob.smith@example.com` and
> `carol.davis@example.com` have the passwords `password1`, `password2` and `password3`. They are stored as
> `{bcrypt}` hashes in `team_track.sql` (Spring's `DelegatingPasswordEncoder` format). To create a new hash, encode the
> password with `PasswordEncoderFactories.createDelegatingPasswordEncoder()`.

## API overview

Every request below needs the `Authorization: Bearer <token>` header (see [Authentication](#authentication)).

| Resource         | Base path           | Endpoints                                                                                                      |
|------------------|---------------------|----------------------------------------------------------------------------------------------------------------|
| Auth             | `/auth`             | `POST /login` (public)                                                                                         |
| Employees        | `/employees`        | `GET /`, `GET /{email}`, `POST /`, `PUT /{id}`, `DELETE /{id}`                                                 |
| Teams            | `/teams`            | `GET /`, `GET /{name}`, `POST /`, `PUT /{id}`, `DELETE /{id}`                                                  |
| Team memberships | `/team-memberships` | `GET /`, `GET /{id}`, `GET /team/{teamId}`, `GET /employee/{employeeId}`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Projects         | `/projects`         | `GET /`, `GET /{id}`, `GET /team/{teamId}`, `POST /`, `PUT /{id}`, `DELETE /{id}`                              |
| Tasks            | `/tasks`            | `GET /`, `GET /{id}`, `GET /project/{projectId}`, `GET /employee/{employeeId}`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Comments         | `/comments`         | `GET /`, `GET /{id}`, `GET /task/{taskId}`, `POST /`, `PUT /{id}`, `DELETE /{id}`                              |

Employees have `email`, `fullName` and `password` fields. Seeded IDs are not sequential (for example employees are
`1`, `51`, `101`), so list a resource first to find valid IDs:

```bash
curl http://localhost:8080/employees -H "Authorization: Bearer <token>"
```

## Configuration

Settings live in `src/main/resources/application.properties`. The database is in-memory, so data resets on every restart.
The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`).

| Property                      | Purpose                                                          | Default                    |
|-------------------------------|------------------------------------------------------------------|----------------------------|
| `security.jwt.secret-key`     | Base64-encoded HS256 signing key (at least 256 bits)             | dev-only key in the file   |
| `security.jwt.expiration-time`| Token lifetime in milliseconds                                   | `3600000` (1 hour)         |

Override the key with the `SECURITY_JWT_SECRET_KEY` environment variable. Generate one with `openssl rand -base64 32`.
The default key is committed for local development only; never use it in a real deployment.

## Security status

Authentication is JWT-based and every endpoint except login and the H2 console requires a valid token.
There is no authorization yet: any authenticated employee can call any endpoint. Planned: role-based access control
(RBAC) so permissions depend on a user's team role (for example, `LEAD` vs. `MEMBER`).

## Roadmap

- [x] JWT authentication (replacing basic auth)
- [ ] Add role-based access control (RBAC) based on team roles
- [ ] Frontend
- [ ] Persistent database
