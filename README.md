# Microservices Platform

A Spring Boot microservices demo built around service discovery, gateway routing, JWT-based access control, and operational monitoring.

## Services

- `discovery-server`: Eureka service registry on `8761`
- `api-gateway`: single entry point and JWT gateway on `8080`
- `user-service`: login and user-protected endpoints on `8081`
- `order-service`: order endpoints on `8082`
- `payment-service`: payment endpoints on `8083`
- `admin-server`: Spring Boot Admin UI on `9090`
- `infra/`: Docker Compose, Prometheus, Grafana, and dashboard provisioning

## Tech Stack

- Java 21
- Spring Boot
- Spring Cloud Gateway
- Eureka
- Spring Security
- Spring Boot Admin
- Prometheus
- Grafana
- Docker Compose

## Repo Layout

```text
.
├── admin-server/
├── api-gateway/
├── discovery-server/
├── infra/
├── order-service/
├── payment-service/
├── user-service/
└── spring-microservices-monitoring-guide.md
```

## Quick Start With Docker

1. Move into the infra folder:

```bash
cd infra
```

2. Review or update `.env` if needed.

3. Start the stack:

```bash
docker compose --env-file .env up --build
```

4. Open the main UIs:

- Eureka: `http://localhost:8761`
- Gateway UI: `http://localhost:8080`
- Admin Server: `http://localhost:9090`
- Prometheus: `http://localhost:9091`
- Grafana: `http://localhost:3000`

## Local Run Order

Run the services in this order if you want to start them without Docker:

1. `discovery-server`
2. `user-service`
3. `order-service`
4. `payment-service`
5. `api-gateway`
6. `admin-server`

Each service uses the Maven wrapper, for example:

```bash
cd discovery-server
./mvnw spring-boot:run
```

Repeat from each service directory as needed.

## Default Environment

The repo already includes defaults in `infra/.env`:

- `DISCOVERY_SERVER_PORT=8761`
- `API_GATEWAY_PORT=8080`
- `USER_SERVICE_PORT=8081`
- `ORDER_SERVICE_PORT=8082`
- `PAYMENT_SERVICE_PORT=8083`
- `ADMIN_SERVER_PORT=9090`
- `PROMETHEUS_PORT=9091`
- `GRAFANA_PORT=3000`

Important defaults:

- Actuator user: `actuator` / `actuator-secret`
- Admin Server user: `admin` / `admin-secret`
- Grafana user: `grafana` / `grafana-secret`
- JWT secret and gateway secret are development defaults and should be replaced outside local/demo use

## Demo Credentials

The user service contains in-memory demo users:

- `user` / `user123`
- `admin` / `admin123`

Use `admin/admin123` to access the admin-only protected endpoints.

## API Routes

These routes are exposed through the gateway on `http://localhost:8080`:

- `POST /api/users/login`
- `GET /api/users/ping`
- `GET /api/users/authenticated`
- `GET /api/orders/ping`
- `GET /api/orders/authenticated`
- `GET /api/payments/ping`
- `GET /api/payments/authenticated`

Example login:

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Then call a protected endpoint with the returned bearer token:

```bash
curl http://localhost:8080/api/orders/authenticated \
  -H "Authorization: Bearer <token>"
```

## Monitoring

- Prometheus scrapes the Spring Boot Actuator `/actuator/prometheus` endpoints
- Grafana is pre-provisioned with dashboards from `infra/grafana/dashboards`
- Spring Boot Admin provides service visibility and health inspection

## Notes

- The gateway root path redirects to `/ui/index.html`
- The gateway UI can log in, decode the JWT payload, and call protected downstream routes
- `spring-microservices-monitoring-guide.md` contains a longer setup and architecture walkthrough
