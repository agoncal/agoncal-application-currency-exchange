# Currency Exchange Application

A microservices-based currency exchange application built with Quarkus.

## Services

- **[Currency Service](currency/README.md)** - gRPC service providing real-time USD-based exchange rates
- **[Trades Service](trades/README.md)** - REST service for executing currency trades
- **[Portfolio Service](portfolio/README.md)** - Web application for managing user portfolios and executing trades

## Building and Executing an executable JAR

```bash
quarkus build --clean --no-tests

java -jar target/quarkus-app/quarkus-run.jar
```

## Building and Executing a native binary

```bash
quarkus build --clean --no-tests --native

./target/currency-1.0.0-SNAPSHOT-runner
```

## Building and Executing an executable JAR Docker Image

Build the Docker images from the root directory (example with currency):

```bash
quarkus image build docker -Dquarkus.container-image.tag=jvm

docker run -i --rm -p 8082:8082 currencyexchange/currency:jvm

grpcurl --plaintext localhost:8082 list
grpcurl --plaintext localhost:8082 describe currency.ExchangeRateService
grpcurl --plaintext localhost:8082 currency.ExchangeRateService/GetAllCurrentRates
grpcurl --plaintext -d '{"currency_code": "AUD"}' localhost:8082 currency.ExchangeRateService/GetCurrentRate
```

## Building and Executing a native binary Docker Image

Build the Docker images from the root directory (example with currency):

```bash
quarkus image build docker --native -Dquarkus.native.container-build=true -Dquarkus.container-image.tag=native

docker run -i --rm -p 8082:8082 currencyexchange/currency:native
```

## Executing all JVM Docker containers

Start all services with Docker Compose:

```bash
docker compose -p currencyexchange-jvm -f docker-compose-jvm.yml up -d
```

Access the application at `http://localhost:8080`

## Executing all Native Docker containers

Start all services with Docker Compose:

```bash
docker compose -p currencyexchange-native -f docker-compose-native.yml up -d
```

Access the application at `http://localhost:8080`
