# Exchange Rate Microservice

## Specification

### Overview

**Role**: gRPC service providing real-time USD-based currency exchange rates
**Technology**: Quarkus + gRPC + Protobuf (no storage, no database)
**Port**: 8082 (gRPC)

### gRPC Service Definition

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "org.agoncal.application.currencyexchange.rates";
option java_outer_classname = "ExchangeRateServiceProto";

package exchangerates;

service ExchangeRateService {
    rpc GetAllCurrentRates(Empty) returns (ExchangeRateListResponse);
    rpc GetCurrentRate(CurrencyRequest) returns (ExchangeRateResponse);
}

message Empty {}

message Currency {
    string code = 1;    // EUR, GBP, JPY, CHF, CAD, AUD
    string name = 2;    // Euro, British Pound, Japanese Yen
    string symbol = 3;  // €, £, ¥
}

message ExchangeRate {
    Currency currency = 1;     // Target currency (EUR, GBP, JPY, etc.)
    double rate = 2;           // Exchange rate (1 USD = 0.9217 EUR)
    string timestamp = 3;      // When the rate was calculated (ISO format)
}

message CurrencyRequest {
    string currency_code = 1;  // EUR, GBP, JPY, etc.
}

message ExchangeRateListResponse {
    repeated ExchangeRate rates = 1;
}

message ExchangeRateResponse {
    ExchangeRate rate = 1;
}
```

### gRPC Service Methods

The `ExchangeRateService` provides the following RPC methods:

```
GetAllCurrentRates(Empty) -> ExchangeRateListResponse         - Get all current USD exchange rates
GetCurrentRate(CurrencyRequest) -> ExchangeRateResponse       - Get specific USD rate (USD to EUR, etc.)
```

### Sample gRPC Responses

#### GetAllCurrentRates(Empty) → ExchangeRateListResponse
```protobuf
rates {
  currency {
    code: "AUD"
    name: "Australian Dollar"
    symbol: "A$"
  }
  rate: 1.5234
  timestamp: "2024-01-15T10:30:15.123"
}
rates {
  currency {
    code: "EUR"
    name: "Euro"
    symbol: "€"
  }
  rate: 0.9217
  timestamp: "2024-01-15T10:30:15.123"
}
rates {
  currency {
    code: "GBP"
    name: "British Pound"
    symbol: "£"
  }
  rate: 0.7905
  timestamp: "2024-01-15T10:30:15.123"
}
# ... more rates
```

#### GetCurrentRate(CurrencyRequest{currency_code: "EUR"}) → ExchangeRateResponse
```protobuf
rate {
  currency {
    code: "EUR"
    name: "Euro"
    symbol: "€"
  }
  rate: 0.9217
  timestamp: "2024-01-15T10:30:15.123"
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
- **On-demand calculation**: Each gRPC call generates fresh rates via `RatesService`
- **Timestamp precision**: LocalDateTime converted to ISO string format
- **Rate algorithm**: `baseRate + sin(currentTime + currencySeed) * fluctuationFactor`
- **Rounding**: Rates rounded to 4 decimal places (JPY to 2 decimal places)
- **No caching**: Pure stateless service using @ApplicationScoped and @GrpcService
- **Dependency injection**: Uses @ConfigProperty for fluctuation factor configuration

### Configuration
```properties
quarkus.grpc.server.port=8082
quarkus.grpc.server.host=0.0.0.0
exchange-rates.fluctuation-factor=0.02
quarkus.application.name=Exchange Rate Micro Service

# Enable gRPC reflection for service discovery
quarkus.grpc.server.enable-reflection-service=true
```

### Health Checks
- `GET /q/health` - Service health status (HTTP endpoint)
- `GET /q/metrics` - Performance metrics (HTTP endpoint)
- gRPC reflection enabled for service discovery


### gRPC Client Usage

#### Java Client Example
```java
@GrpcClient
ExchangeRateService exchangeRateService;

// Get all current rates
ExchangeRateListResponse rates = exchangeRateService
    .getAllCurrentRates(Empty.newBuilder().build())
    .await().atMost(Duration.ofSeconds(5));

// Get specific rate
CurrencyRequest request = CurrencyRequest.newBuilder()
    .setCurrencyCode("EUR")
    .build();

ExchangeRateResponse response = exchangeRateService
    .getCurrentRate(request)
    .await().atMost(Duration.ofSeconds(5));

ExchangeRate rate = response.getRate();
// Process rate...
```

#### Client Configuration
```properties
quarkus.grpc.clients.exchangeRateService.host=localhost
quarkus.grpc.clients.exchangeRateService.port=8082
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8082/q/dev/> (HTTP port for dev UI, gRPC runs on same port).

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
