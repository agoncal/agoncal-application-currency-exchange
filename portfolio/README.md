# Currency Exchange - Portfolio Service

## Specifications 

### Overview
**Role**: UI frontend + REST backend for managing user currency portfolios
**Technology**: Quarkus + REST + JSON-B + H2 Database + HTML + Bootstrap + Quarkus Renarde + Quarkus Qute
**Port**: 8080

### Entities persisted in the H2 database

```java
public class Portfolio {
    public Long id;
    public String userId;
    public String currency;
    public BigDecimal balance;
    public LocalDateTime lastUpdated;
}
```

### DTOs sent to the Trades and Rates microservices

```java
public class Trade {
    public Long id;
    public String userId;
    public String fromCurrency;
    public String toCurrency;
    public BigDecimal fromAmount;
    public BigDecimal toAmount;
    public BigDecimal exchangeRate;
    public LocalDateTime timestamp;
    public TradeStatus status;
}

public enum TradeStatus {
    PENDING,     // Trade request received, waiting for processing
    COMPLETED,   // Trade successfully executed
    FAILED      // Trade failed due to business logic (insufficient funds, invalid currency, etc.)
}

public class Currency {
    public String code;        // EUR, USD, GBP
    public String name;        // Euro, US Dollar
    public String symbol;      // €, $, £
}

public class ExchangeRate {
    public String fromCurrency;
    public String toCurrency;
    public BigDecimal rate;
    public LocalDateTime timestamp;
}
```

### REST Endpoints

The `PortfolioResource` defines the following APIs:

```
GET    /api/portfolio/{userId}           - Get user's portfolio balances
GET    /api/portfolio/{userId}/history   - Get user's trade history
GET    /api/portfolio/rates              - Get all current currency exchange rates
POST   /api/portfolio/exchange           - Execute currency exchange
GET    /api/portfolio/trades             - Get all trades (admin view)
```

### Transactional Service

The `PortfolioResource` delegates each invocation to the `PortfolioService`


### UI Endpoint
```
GET    /                                 - Single-page dashboard with:
                                         • Exchange rates (top section)
                                         • Currency exchange form (middle)
                                         • Trade history (bottom section)
                                         • Refresh button for rates
```

### External Service Calls
- **Exchange Rate Service** (REST): Get current exchange rates via `/api/portfolio/rates` endpoint
  ```
  GET http://localhost:8082/api/rates
  ```
- **Trading Service** (gRPC): Execute trades when user submits exchange form
  ```
  ExecuteTrade(userId, fromCurrency, toCurrency, amount)
  ```

### API Usage Flow for Single Page:
1. **Page Load**:
    - Call `GET /api/portfolio/user123` - Get portfolio balances
    - Call `GET /api/portfolio/rates` - Get current rates for display
    - Call `GET /api/portfolio/user123/history` - Get trade history

2. **Exchange Form Submit**:
    - Call `POST /api/portfolio/exchange` - Execute the trade
    - Refresh portfolio balances and trade history

3. **Manual Refresh**:
    - User clicks refresh button
    - Call `GET /api/portfolio/rates` - Get updated exchange rates

### Configuration
```properties
quarkus.http.port=8081
exchange-rate-service/mp-rest/url=http://localhost:8082
trading-service.host=localhost
trading-service.port=9001
```

This streamlined approach provides a clear API structure where the Portfolio Service acts as a facade, aggregating data from external services and presenting it through its own consistent `/api/portfolio` namespace.

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
