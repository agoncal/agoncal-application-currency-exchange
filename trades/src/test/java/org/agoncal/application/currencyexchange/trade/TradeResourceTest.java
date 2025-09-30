package org.agoncal.application.currencyexchange.trade;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
class TradeResourceTest {

    @Test
    void shouldExecuteTrade() {
        Trade trade = new Trade("user123", BigDecimal.valueOf(100), "EUR", BigDecimal.valueOf(0.92));

        given()
            .contentType(ContentType.JSON)
            .body(trade)
            .when()
            .post("/api/trades")
            .then()
            .statusCode(204);
    }

    @Test
    void shouldNotExecuteTradeInvalidData() {
        Trade trade = new Trade("", BigDecimal.valueOf(-10), "", BigDecimal.valueOf(-1));

        given()
            .contentType(ContentType.JSON)
            .body(trade)
            .when()
            .post("/api/trades")
            .then()
            .statusCode(500);
    }

    @Test
    void shouldGetAllTradesEmpty() {
        given()
            .when()
            .get("/api/trades/newuser")
            .then()
            .statusCode(200)
            .body("size()", is(0));
    }

    @Test
    void shouldExecuteAndGetTrades() {
        // First, execute a trade
        Trade trade = new Trade("user456", BigDecimal.valueOf(100), "EUR", BigDecimal.valueOf(0.92));

        given()
            .contentType(ContentType.JSON)
            .body(trade)
            .when()
            .post("/api/trades")
            .then()
            .statusCode(204);

        // Then retrieve trades for the user
        given()
            .when()
            .get("/api/trades/user456")
            .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].userId", is("user456"))
            .body("[0].toCurrency", is("EUR"))
            .body("[0].usdAmount", is(100))
            .body("[0].exchangeRate", is(0.92f));
    }

    @Test
    void testGetTradesInvalidUserId() {
        // Test with empty userId handled by our service logic
        given()
            .when()
            .get("/api/trades/")  // Empty path
            .then()
            .statusCode(405); // Method not allowed for empty path
    }
}