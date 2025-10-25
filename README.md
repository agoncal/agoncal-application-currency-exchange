# Currency Exchange Application

A microservices-based currency exchange application built with Quarkus (Java 21).

## Architecture

This application demonstrates a microservices architecture with three independent services:

- **[Portfolio Service](portfolio/README.md)** (Port 8080) - Web UI for managing user portfolios and executing trades
  - Technology: Quarkus + REST + JSON-B + H2 Database + Quarkus Renarde + Qute
  - Aggregates data from Currency and Trades services

- **[Currency Service](currency/README.md)** (Port 8082) - gRPC service providing real-time USD-based exchange rates
  - Technology: Quarkus + gRPC + Protobuf
  - Stateless service with algorithmic rate generation

- **[Trades Service](trades/README.md)** (Port 8083/9001) - REST service for executing currency trades
  - Technology: Quarkus + REST + Jackson + Hibernate Validator
  - Stateless service with in-memory HashMap for trade history

### Trade Status Flow

Trades track their lifecycle through status values:
- **CREATED**: Initial status when Portfolio service creates a trade
- **PENDING**: Trade sent to Trades service but not yet completed (when exchange rate is 0)
- **COMPLETED**: Trade successfully executed by Trades service (when exchange rate > 0)

## Development

### Running Services Locally

Run each service in dev mode from its directory:

```bash
# Portfolio Service (Port 8080)
cd portfolio && ./mvnw quarkus:dev

# Currency Service (Port 8082)
cd currency && ./mvnw quarkus:dev

# Trades Service (Port 9001)
cd trades && ./mvnw quarkus:dev
```

Access the application at `http://localhost:8080`

**Test Credentials:**
- Email: `john.doe@example.com`, `jane.smith@example.com`, or `bob.johnson@example.com`
- Password: `password`

### Building from Root

```bash
# Build all services
./mvnw package

# Run tests
./mvnw test
```

## Building and Executing an Executable JAR

From a service directory:

```bash
quarkus build --clean --no-tests
java -jar target/quarkus-app/quarkus-run.jar
```

Or with Maven:

```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

## Building and Executing a Native Binary

```bash
# Using Quarkus CLI
quarkus build --clean --no-tests --native

# Or with Maven
./mvnw package -Dnative

# Or build in container (no GraalVM required)
./mvnw package -Dnative -Dquarkus.native.container-build=true

# Run the native binary
./target/*-runner
```

## Docker Deployment

### Building Docker Images

Build images from each service directory:

```bash
# JVM image
quarkus image build docker -Dquarkus.container-image.tag=jvm

# Native image
quarkus image build docker --native -Dquarkus.native.container-build=true -Dquarkus.container-image.tag=native
```

### Running Individual Containers

```bash
# Currency Service
docker run -i --rm -p 8082:8082 currencyexchange/currency:jvm

# Trades Service
docker run -i --rm -p 8083:8083 currencyexchange/trades:jvm

# Portfolio Service
docker run -i --rm -p 8080:8080 \
  -e QUARKUS_GRPC_CLIENTS_CURRENCY_HOST=currency \
  -e QUARKUS_REST_CLIENT_TRADES_URL=http://trades:8083 \
  currencyexchange/portfolio:jvm
```

### Testing gRPC Service

```bash
grpcurl --plaintext localhost:8082 list
grpcurl --plaintext localhost:8082 describe exchangerates.ExchangeRateService
grpcurl --plaintext localhost:8082 exchangerates.ExchangeRateService/GetAllCurrentRates
grpcurl --plaintext -d '{"currency_code": "AUD"}' localhost:8082 exchangerates.ExchangeRateService/GetCurrentRate
```

### Running All Services with Docker Compose

**JVM containers:**

```bash
docker compose -p currencyexchange-jvm -f docker-compose-jvm.yml up -d
```

**Native containers:**

```bash
docker compose -p currencyexchange-native -f docker-compose-native.yml up -d
```

Access the application at `http://localhost:8080`

**Stop services:**

```bash
docker compose -p currencyexchange-jvm -f docker-compose-jvm.yml down
docker compose -p currencyexchange-native -f docker-compose-native.yml down
```

## Service Endpoints

- **Portfolio**: http://localhost:8080 (Web UI)
- **Currency**: localhost:8082 (gRPC)
- **Trades**: http://localhost:8083 (REST API)

Health checks and Dev UI:
- Portfolio: http://localhost:8080/q/health, http://localhost:8080/q/dev/
- Currency: http://localhost:8082/q/health, http://localhost:8082/q/dev/
- Trades: http://localhost:8083/q/health, http://localhost:8083/q/dev/
