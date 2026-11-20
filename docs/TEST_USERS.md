# Test users

Development-only logins seeded by `src/main/resources/team_track.sql`. Do not reuse these passwords anywhere real.

| Name          | Username (email)            | Password    |
|---------------|-----------------------------|-------------|
| Alice Johnson | `alice.johnson@example.com` | `password1` |
| Bob Smith     | `bob.smith@example.com`     | `password2` |
| Carol Davis   | `carol.davis@example.com`   | `password3` |

The database is in-memory, so these users are recreated on every restart. Any employee created through
`POST /employees` can also log in with the password supplied at creation.

## Getting a token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice.johnson@example.com","password":"password1"}'
# -> {"token":"eyJ...","expiresIn":3600000}
```

A wrong email or password returns `401`. The token is valid for `expiresIn` milliseconds (1 hour by default).

## Calling the API

```bash
TOKEN="<paste token here>"
curl http://localhost:8080/employees -H "Authorization: Bearer $TOKEN"
```

Without a valid token every endpoint except `POST /auth/login` and `/h2-console` returns `401`.

## Postman / Insomnia

1. `POST http://localhost:8080/auth/login` with a JSON body of `{"email": "...", "password": "..."}`.
2. Copy `token` from the response.
3. On other requests, set Authorization to **Bearer Token** and paste it (or send the header
   `Authorization: Bearer <token>`).

## Changing or adding test users

Passwords are stored as `{bcrypt}` hashes. To make a new hash, encode the password with
`PasswordEncoderFactories.createDelegatingPasswordEncoder()` and put the result in `team_track.sql`, or create the
user via `POST /employees` (the API hashes the password for you).
