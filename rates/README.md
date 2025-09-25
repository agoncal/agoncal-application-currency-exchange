# Exchange Rate Microservice

## Specification

### Overview

**Role**: REST service providing real-time USD-based currency exchange rates
**Technology**: Quarkus + REST + JSON-B (no storage, no database)
**Port**: 8082

### DTOs

```java
public record Currency(
    String code,    // EUR, GBP, JPY, CHF, CAD, AUD
    String name,    // Euro, British Pound, Japanese Yen
    String symbol   // €, £, ¥
) {}

public record ExchangeRate(
    Currency currency,       // Target currency (EUR, GBP, JPY, etc.)
    BigDecimal rate,         // Exchange rate (1 USD = 0.9217 EUR)
    LocalDateTime timestamp  // When the rate was calculated
) {}
```

### REST Endpoints

The `RatesResource` defines the following APIs:

```
GET    /api/rates            - Get all current USD exchange rates
GET    /api/rates/{to}       - Get specific USD rate (USD to EUR: /api/rates/EUR)
```

### Sample Responses


#### GET /api/rates
```json
[
  {
    "currency": {
      "code": "AUD",
      "name": "Australian Dollar",
      "symbol": "A$"
    },
    "rate": 1.5234,
    "timestamp": "2024-01-15T10:30:15.123"
  },
  {
    "currency": {
      "code": "EUR",
      "name": "Euro",
      "symbol": "€"
    },
    "rate": 0.9217,
    "timestamp": "2024-01-15T10:30:15.123"
  },
  {
    "currency": {
      "code": "GBP",
      "name": "British Pound",
      "symbol": "£"
    },
    "rate": 0.7905,
    "timestamp": "2024-01-15T10:30:15.123"
  },
  {
    "currency": {
      "code": "JPY",
      "name": "Japanese Yen",
      "symbol": "¥"
    },
    "rate": 149.25,
    "timestamp": "2024-01-15T10:30:15.123"
  }
]
```

#### GET /api/rates/EUR
```json
{
  "currency": {
    "code": "EUR",
    "name": "Euro",
    "symbol": "€"
  },
  "rate": 0.9217,
  "timestamp": "2024-01-15T10:30:15.123"
}
```

### Real-Time Rate Generation
- **No storage**: Rates calculated on-demand for each request
- **Algorithm**: Generate rates using current timestamp + currency-specific seed
- **Fluctuation**: Configurable fluctuation factor (default: 0.02)
- **Base rates**: USD to other currencies (1 USD = X target currency)
- **Supported currencies**: AUD, CAD, CHF, EUR, GBP, JPY (hardcoded)
- **Currency seeds**: Each currency has a unique seed (AUD: 1000L, CAD: 2000L, etc.)

### Sample Real-Time Rates
```
1 USD = 0.9217 EUR  (USD to Euro)
1 USD = 0.7905 GBP  (USD to British Pound)
1 USD = 149.25 JPY  (USD to Japanese Yen)
1 USD = 0.9156 CHF  (USD to Swiss Franc)
1 USD = 1.3425 CAD  (USD to Canadian Dollar)
1 USD = 1.5234 AUD  (USD to Australian Dollar)
```

### Business Logic
- **On-demand calculation**: Each API call generates fresh rates via `RatesService`
- **Timestamp precision**: LocalDateTime with nanosecond precision
- **Rate algorithm**: `baseRate + sin(currentTime + currencySeed) * fluctuationFactor`
- **Rounding**: Rates rounded to 4 decimal places (JPY to 2 decimal places)
- **No caching**: Pure stateless service using @ApplicationScoped
- **Currency validation**: Return 404 for unsupported currencies
- **Dependency injection**: Uses @ConfigProperty for fluctuation factor configuration

### Configuration
```properties
quarkus.http.port=8082
exchange-rates.fluctuation-factor=0.02
quarkus.application.name=Exchange Rate Micro Service
```

### Health Checks
- `GET /q/health` - Service health status
- `GET /q/metrics` - Performance metrics
- `GET /q/openapi` - OpenAPI specification

### Error Responses
- **404**: Unsupported currency code with JSON error message
  ```json
  {"error": "Unsupported currency: XYZ"}
  ```
- **500**: Rate calculation error (if thrown by service layer)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/currency-exchange-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST JSON-B ([guide](https://quarkus.io/guides/rest#json-serialisation)): JSON-B serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
