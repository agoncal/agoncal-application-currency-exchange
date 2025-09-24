# Trading Microservice

## Specifications

### Overview
**Role**: gRPC service for executing USD-based currency trades and business logic
**Technology**: Quarkus + gRPC + JSON-B - H2 database
**Port**: 9001 (gRPC)

### Persistent Entities

```java
public class TradeRequest {
    public String userId;
    public String fromCurrency;    // Always "USD" 
    public String toCurrency;      // EUR, GBP, JPY, etc.
    public BigDecimal usdAmount;   // Amount in USD to convert
}

public class TradeResponse {
    public Long tradeId;           // Generated unique trade ID
    public TradeStatus status;     // PENDING, COMPLETED, FAILED, REJECTED, TIMEOUT
    public BigDecimal usdAmount;   // Original USD amount
    public String toCurrency;      // Target currency
    public BigDecimal convertedAmount;  // Amount received in target currency
    public BigDecimal exchangeRate;     // Rate used for conversion
    public String message;         // Success/error message
    public LocalDateTime timestamp;
}

public enum TradeStatus {
    PENDING, COMPLETED, FAILED, REJECTED, TIMEOUT
}
```

### gRPC Service Definition
```protobuf
service TradingService {
    rpc ExecuteTrade(TradeRequest) returns (TradeResponse);
    rpc ValidateTrade(TradeRequest) returns (ValidationResponse);
}

message TradeRequest {
    string user_id = 1;
    string from_currency = 2;    // Always "USD"
    string to_currency = 3;      // EUR, GBP, JPY, etc.
    double usd_amount = 4;
}

message TradeResponse {
    int64 trade_id = 1;
    string status = 2;           // COMPLETED, FAILED, etc.
    double usd_amount = 3;
    string to_currency = 4;
    double converted_amount = 5;
    double exchange_rate = 6;
    string message = 7;
    string timestamp = 8;
}

message ValidationResponse {
    bool is_valid = 1;
    string error_message = 2;
}
```

### Business Logic

#### ExecuteTrade
- **Input validation**: Check userId, currency codes, amount > 0
- **Currency validation**: Ensure toCurrency is supported (not USD)
- **Rate retrieval**: Get current USD exchange rate (simulated)
- **Amount calculation**: `convertedAmount = usdAmount * exchangeRate`
- **Trade execution**: Generate trade ID and timestamp
- **Response**: Return trade confirmation with all details

#### ValidateTrade
- **Pre-validation**: Check trade parameters without execution
- **Business rules**: Validate currency support, amount limits
- **Return**: Validation result with error details if invalid

### Sample Trade Flows

#### Successful Trade
```
Request:  userId="user123", fromCurrency="USD", toCurrency="EUR", usdAmount=100.00
Response: tradeId=12345, status=COMPLETED, usdAmount=100.00, toCurrency="EUR", 
          convertedAmount=92.17, exchangeRate=0.9217, message="Trade completed successfully"
```

#### Failed Trade
```
Request:  userId="user123", fromCurrency="USD", toCurrency="XYZ", usdAmount=100.00
Response: tradeId=12346, status=REJECTED, message="Unsupported currency: XYZ"
```

### Stateless Design
- **No storage**: Each trade is processed independently
- **No state**: Service doesn't track trade history
- **Rate simulation**: Generate exchange rates using algorithm (not external calls)
- **Trade ID generation**: Use timestamp + random number
- **Pure business logic**: Focus on trade execution rules

### Simulated Exchange Rates
```java
// Internal rate calculation (no external service calls)
private BigDecimal getExchangeRate(String toCurrency) {
    switch(toCurrency) {
        case "EUR": return BigDecimal.valueOf(0.9217 + randomFluctuation());
        case "GBP": return BigDecimal.valueOf(0.7905 + randomFluctuation());
        case "JPY": return BigDecimal.valueOf(149.25 + randomFluctuation());
        // ... other currencies
        default: throw new UnsupportedCurrencyException();
    }
}
```

### Configuration
```properties
quarkus.grpc.server.port=9001
quarkus.grpc.server.host=0.0.0.0
trading.supported-currencies=EUR,GBP,JPY,CHF,CAD,AUD
trading.min-trade-amount=1.00
trading.max-trade-amount=10000.00
```

### Error Handling
- **REJECTED**: Invalid parameters, unsupported currency
- **FAILED**: Business logic failure, amount out of range
- **TIMEOUT**: Simulated processing timeout (for fault tolerance demos)

### Health Checks
- `GET /q/health` - Service health status (HTTP endpoint for health)
- `GET /q/metrics` - Performance metrics
- gRPC reflection enabled for service discovery

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
