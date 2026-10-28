# FestHub-Microservice
A live entertainment platform that can be used by concert organizers, booking agencies, and event coordinators to manage global festival lineups, talent scheduling, marketing assets, and ticket pools. This application is implemented using a microservice architecture.

## Gateway and team development

The gateway is the client-facing entry point on port `8080`. It forwards requests without changing the path:

| Public path | Service |
| --- | --- |
| `/api/artist/**` | artist |
| `/api/category/**` | booking-category |
| `/api/events/**` | concert-event |
| `/api/media/**` | promo-media |

Services should implement endpoints under their assigned public path. For example, an artist endpoint should be reachable at `/api/artist/...`. The gateway route targets can be changed independently with `ARTIST_SERVICE_URL`, `BOOKING_CATEGORY_SERVICE_URL`, `CONCERT_EVENT_SERVICE_URL`, and `PROMO_MEDIA_SERVICE_URL`.

### Run the complete stack in Docker

1. Copy `.env.example` to `.env` and set `DB_USER` and `DB_PASSWORD` to development-only values.
2. Start the stack from the repository root:

   ```powershell
   docker compose up --build
   ```

3. Open `http://localhost` for the client. API requests go through the client’s Nginx proxy to the gateway. The gateway is also available directly at `http://localhost:8080`.

Compose routes to the containers by their service names. The database and service ports published on the host are bound to loopback only, for local development and testing.

### Run the gateway and services locally

The local gateway profile targets `localhost:8081` through `localhost:8084`. Start the databases in Docker, but not the application containers:

```powershell
docker compose up -d artist-db category-db event-db media-db
```

In each service’s own directory, run `.\mvnw.cmd spring-boot:run` with the matching environment variables below. Set `DB_USER` and `DB_PASSWORD` to the values in your `.env`.

| Service | `SERVER_PORT` | `DB_URL` |
| --- | ---: | --- |
| artist | `8081` | `jdbc:postgresql://localhost:5433/artistdb` |
| booking-category | `8082` | `jdbc:postgresql://localhost:5434/categorydb` |
| concert-event | `8083` | `jdbc:postgresql://localhost:5435/eventdb` |
| promo-media | `8084` | `jdbc:postgresql://localhost:5436/mediadb` |

For example, to run artist from PowerShell:

```powershell
$env:SERVER_PORT = '8081'
$env:DB_URL = 'jdbc:postgresql://localhost:5433/artistdb'
$env:DB_USER = 'your-local-db-user'
$env:DB_PASSWORD = 'your-local-db-password'
.\mvnw.cmd spring-boot:run
```

Start the gateway from its directory with the local profile:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'local'
.\mvnw.cmd spring-boot:run
```

For a gateway running on the host, use `http://localhost:<published-port>` for every backend, whether that backend is a local process or a Compose container. For a gateway running in Compose, use Compose service names such as `http://artist:8080`. If a Compose gateway must call a service running directly on the host, use `http://host.docker.internal:<service-port>`. The `local` profile defaults all targets to localhost; without it, the defaults use Compose service names. Do not commit `.env` or credentials.
