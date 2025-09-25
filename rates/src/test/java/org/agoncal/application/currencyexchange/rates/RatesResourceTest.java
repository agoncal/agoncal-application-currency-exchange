package org.agoncal.application.currencyexchange.rates;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RatesResourceTest {

    @Test
    void testGetAllRates() {
        given()
          .when().get("/api/rates")
          .then()
             .statusCode(200)
             .contentType("application/json")
             .body("size()", is(6))
             .body("currency.code", hasItems("EUR", "GBP", "JPY", "CHF", "CAD", "AUD"))
             .body("rate", everyItem(notNullValue()))
             .body("timestamp", everyItem(notNullValue()));
    }

    @Test
    void testGetSpecificRateEUR() {
        given()
          .when().get("/api/rates/EUR")
          .then()
             .statusCode(200)
             .contentType("application/json")
             .body("currency.code", is("EUR"))
             .body("rate", notNullValue())
             .body("timestamp", notNullValue());
    }

    @Test
    void testGetSpecificRateUnsupported() {
        given()
          .when().get("/api/rates/XYZ")
          .then()
             .statusCode(404)
             .contentType("application/json")
             .body("error", containsString("Unsupported currency: XYZ"));
    }
}