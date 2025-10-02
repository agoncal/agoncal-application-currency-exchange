# Currency Exchange Application

A microservices-based currency exchange application built with Quarkus.

## Services

- **[Currency Service](currency/README.md)** - gRPC service providing real-time USD-based exchange rates
- **[Trades Service](trades/README.md)** - REST service for executing currency trades
- **[Portfolio Service](portfolio/README.md)** - Web application for managing user portfolios and executing trades

## Running with Docker

Build the Docker images from the root directory:

```bash
mvn clean package -Dmaven.test.skip=true
```

Start all services with Docker Compose:

```bash
docker compose -p currencyexchange -f docker-compose-jvm.yml up -d
```

Access the application at `http://localhost:8080`