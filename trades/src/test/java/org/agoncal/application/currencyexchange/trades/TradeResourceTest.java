package org.agoncal.application.currencyexchange.trades;

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
    void testExecuteTrade() {
        Trade trade = new Trade();
        trade.userId = "user123";
        trade.toCurrency = "EUR";
        trade.exchangeRate = new BigDecimal("0.9217");
        trade.usdAmount = new BigDecimal("100.00");

        given()
                .contentType(ContentType.JSON)
                .body(trade)
                .when()
                .post("/api/trades")
                .then()
                .statusCode(200)
                .body("userId", is("user123"))
                .body("toCurrency", is("EUR"))
                .body("usdAmount", is(100.00f))
                .body("exchangeRate", is(0.9217f))
                .body("convertedAmount", is(92.17f))
                .body("status", notNullValue())
                .body("timestamp", notNullValue());
    }

    @Test
    void testExecuteTradeInvalidData() {
        Trade trade = new Trade();
        trade.userId = "user123";
        trade.toCurrency = "EUR";
        trade.exchangeRate = new BigDecimal("-1.0"); // Invalid negative rate
        trade.usdAmount = new BigDecimal("100.00");

        given()
                .contentType(ContentType.JSON)
                .body(trade)
                .when()
                .post("/api/trades")
                .then()
                .statusCode(400)
                .body("error", notNullValue());
    }

    @Test
    void testGetAllTradesEmpty() {
        given()
                .when()
                .get("/api/trades/newuser")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testExecuteAndGetTrades() {
        // First, execute a trade
        Trade trade = new Trade();
        trade.userId = "testuser";
        trade.toCurrency = "GBP";
        trade.exchangeRate = new BigDecimal("0.7905");
        trade.usdAmount = new BigDecimal("50.00");

        given()
                .contentType(ContentType.JSON)
                .body(trade)
                .when()
                .post("/api/trades")
                .then()
                .statusCode(200);

        // Then retrieve trades for the user
        given()
                .when()
                .get("/api/trades/testuser")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].userId", is("testuser"))
                .body("[0].toCurrency", is("GBP"))
                .body("[0].usdAmount", is(50.00f))
                .body("[0].exchangeRate", is(0.7905f));
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