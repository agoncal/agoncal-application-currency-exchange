# Trading Microservice

## Specifications

### Overview
**Role**: REST service for executing USD-based currency trades and business logic
**Technology**: Quarkus + REST + JSON-B + no database (the history of TradeResponses is kept in a HashMap with the user id as a key, and a list of TradeResponses)
**Port**: 9001 (HTTP)

### Persistent Entities

```java
public class Trade {
    public String userId;
    public LocalDateTime timestamp;
    public TradeStatus status;     // PENDING, COMPLETED, FAILED
    public BigDecimal usdAmount;   // Original USD amount
    public String toCurrency;      // Target currency
    public BigDecimal convertedAmount;  // Amount received in target currency
    public BigDecimal exchangeRate;     // Rate used for conversion
}

public enum TradeStatus {
    PENDING, COMPLETED, FAILED
}
```

### REST API Definition
```
POST /api/trades
- Request Body: Trade (JSON)
- Response: Trade (JSON)
- Content-Type: application/json

GET /api/trades/{userId}
- Path Parameter: userId (String)
- Response: List<Trade> (JSON)
- Content-Type: application/json
```

#### JSON Request/Response Examples
```json
// Trade (for the request)
{
    "userId": "user123",
    "toCurrency": "EUR",
    "exchangeRate": 0.9217,
    "usdAmount": 100.00
}

// Trade (for the response)
{
    "userId": "user123",
    "timestamp": "2024-01-15T10:30:45"
    "status": "COMPLETED",
    "usdAmount": 100.00,
    "toCurrency": "EUR",
    "convertedAmount": 92.17,
    "exchangeRate": 0.9217,
}
```

### Business Logic

#### POST /api/trades/execute
- **Input validation**: Check currency codes, exchangeRate and usdAmount > 0
- **Amount calculation**: `convertedAmount = usdAmount * exchangeRate`
- **Random TradeStatus**: TradeStatus should mostly be COMPLETED. But randomly assign the other status (with a lower weight)
- **Trade execution**: Generate trade timestamp
- **Response**: Return trade confirmation with all details as JSON

#### GET /api/trades/{userId}
- **Return**: Returns a list of trades for a given userId as JSON array

### Sample Trade Flows

#### Successful Trade
```
POST /api/trades
Request Body:
{
    "userId": "user123",
    "toCurrency": "EUR",
    "exchangeRate": 0.9217,
    "usdAmount": 100.00
}

Response:
{
    "userId": "user123",
    "status": "COMPLETED",
    "usdAmount": 100.00,
    "toCurrency": "EUR",
    "convertedAmount": 92.17,
    "exchangeRate": 0.9217,
    "timestamp": "2024-01-15T10:30:45"
}
```

### Stateless Design
- **In memory state**: Service stores all trade history in a hashmap
- **Pure business logic**: Focus on trade execution rules


### Configuration
```properties
quarkus.http.port=9001
quarkus.http.host=0.0.0.0
```

### Error Handling
- **FAILED status**: Invalid parameters, wrong amount
- **HTTP 400**: Bad request for invalid input
- **HTTP 404**: User not found for trade history
- **HTTP 500**: Internal server error

### Health Checks
- `GET /q/health` - Service health status
- `GET /q/metrics` - Performance metrics
- `GET /q/openapi` - OpenAPI specification
- `GET /q/swagger-ui` - Swagger UI for API documentation

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
