# LDS Task - Spring Boot Microservices

Repository with two Java/Spring Boot microservices communicating via REST, using MongoDB, Swagger/OpenAPI, tests, and Docker Compose.

## Services

- `clienti-service` (port `8081` on host): customer management
- `conti-service` (port `8082` on host): account management + deposit/withdraw + customer enrichment via REST call to `clienti-service`
- `mongodb` (single container): one MongoDB instance with separate DBs:
  - `clienti_db`
  - `conti_db`

## Tech Stack

- Java 17
- Spring Boot 3.3.x
- Maven (multi-module)
- MongoDB
- springdoc-openapi (Swagger UI)
- JUnit 5 + Spring Boot Test + MockMvc
- Embedded Mongo (Flapdoodle) for tests
- MockWebServer for mocking `clienti-service` in `conti-service` tests
- Lombok

## Project Structure

```text
.
|-- pom.xml
|-- docker-compose.yml
|-- clienti-service
|   |-- Dockerfile
|   |-- pom.xml
|   `-- src
`-- conti-service
    |-- Dockerfile
    |-- pom.xml
    `-- src
```

## Run Locally (without Docker)

Prerequisites:
- Java 17+
- Maven 3.9+
- MongoDB running locally on `localhost:27017`

1. Start `clienti-service`:
```bash
mvn -pl clienti-service spring-boot:run
```

2. Start `conti-service` in another terminal:
```bash
mvn -pl conti-service spring-boot:run
```

Default local configuration:
- `clienti-service`: `http://localhost:8081`
- `conti-service`: `http://localhost:8082`
- `conti-service` calls `clienti-service` via `CLIENTI_SERVICE_URL` (default: `http://localhost:8081`)

## Run with Docker Compose

```bash
docker compose up --build
```

Exposed ports:
- clienti-service: `8081 -> 8080`
- conti-service: `8082 -> 8080`
- mongodb: `27017`

Environment variables used in compose:
- `SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/clienti_db` (clienti-service)
- `SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/conti_db` (conti-service)
- `CLIENTI_SERVICE_URL=http://clienti-service:8080` (conti-service)

## Swagger UI

- Clienti: `http://localhost:8081/swagger-ui/index.html`
- Conti: `http://localhost:8082/swagger-ui/index.html`

(`.../swagger-ui.html` is also configured)

## Tests

Run all tests:
```bash
mvn test
```

Or per module:
```bash
mvn -pl clienti-service test
mvn -pl conti-service test
```

## API Summary

### clienti-service
- `GET /api/clienti`
- `GET /api/clienti/{id}`
- `POST /api/clienti`
- `DELETE /api/clienti/{id}`

### conti-service
- `GET /api/conti`
- `GET /api/conti/{id}`
- `POST /api/conti`
- `PUT /api/conti/{id}`
- `DELETE /api/conti/{id}`
- `POST /api/conti/{id}/deposito`
- `POST /api/conti/{id}/prelievo`

## Inter-Service Behavior

When `GET /api/conti/{id}` is called, `conti-service` queries `clienti-service` (`GET /api/clienti/{clienteId}`) and enriches the response.

Design choice implemented:
- If customer is not found (`404` from clienti-service), account data is returned with `cliente: null`.
- If clienti-service is unreachable or returns other errors, `conti-service` returns `502 Bad Gateway`.

## Example cURL

Create customer:
```bash
curl -X POST http://localhost:8081/api/clienti \
  -H "Content-Type: application/json" \
  -d '{
    "nome":"Mario",
    "cognome":"Rossi",
    "codiceFiscale":"RSSMRA80A01H501U",
    "email":"mario.rossi@example.com",
    "indirizzo":"Via Roma 1, Milano"
  }'
```

Open account:
```bash
curl -X POST http://localhost:8082/api/conti \
  -H "Content-Type: application/json" \
  -d '{
    "iban":"IT60X0542811101000000123456",
    "saldo":1000.00,
    "dataApertura":"2026-03-04",
    "clienteId":"<CLIENTE_ID>"
  }'
```

Deposit:
```bash
curl -X POST http://localhost:8082/api/conti/<CONTO_ID>/deposito \
  -H "Content-Type: application/json" \
  -d '{"importo": 150.00}'
```

Withdraw:
```bash
curl -X POST http://localhost:8082/api/conti/<CONTO_ID>/prelievo \
  -H "Content-Type: application/json" \
  -d '{"importo": 50.00}'
```
