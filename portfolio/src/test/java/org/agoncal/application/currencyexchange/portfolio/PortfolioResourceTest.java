package org.agoncal.application.currencyexchange.portfolio;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class PortfolioResourceTest {

    @Test
    void testGetJohnDoePortfolio() {
        given()
          .when().get("/api/portfolio/john.doe@example.com")
          .then()
             .statusCode(200)
             .contentType("application/json")
             .body("size()", is(7)) // Should return 7 currencies
             .body("user.email", everyItem(is("john.doe@example.com")))
             .body("user.name", everyItem(is("John")))
             .body("user.surname", everyItem(is("Doe")))
             .body("currency", hasItems("USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD"))
             .body("balance", everyItem(notNullValue()))
             .body("lastUpdated", everyItem(notNullValue()));
    }

    @Test
    void testGetJaneSmithPortfolioBalances() {
        given()
          .when().get("/api/portfolio/jane.smith@example.com")
          .then()
             .statusCode(200)
             .contentType("application/json")
             .body("find { it.currency == 'USD' }.balance", is(2000.0f))
             .body("find { it.currency == 'EUR' }.balance", is(1700.0f))
             .body("user.name", everyItem(is("Jane")))
             .body("user.surname", everyItem(is("Smith")));
    }

    @Test
    void testGetBobJohnsonPortfolio() {
        given()
          .when().get("/api/portfolio/bob.johnson@example.com")
          .then()
             .statusCode(200)
             .contentType("application/json")
             .body("size()", is(7))
             .body("find { it.currency == 'USD' }.balance", is(500.0f))
             .body("user.name", everyItem(is("Bob")))
             .body("user.surname", everyItem(is("Johnson")));
    }

    @Test
    void testGetNonExistentUser() {
        // Test that non-existent users return empty list
        given()
          .when().get("/api/portfolio/nonexistent@example.com")
          .then()
             .statusCode(200)
             .contentType("application/json")
             .body("size()", is(0));
    }

    @Test
    void testPortfolioStructure() {
        // Test the structure of portfolio objects
        given()
          .when().get("/api/portfolio/john.doe@example.com")
          .then()
             .statusCode(200)
             .contentType("application/json")
             .body("[0].id", notNullValue())
             .body("[0].user", notNullValue())
             .body("[0].user.id", is(1))
             .body("[0].currency", notNullValue())
             .body("[0].balance", notNullValue())
             .body("[0].lastUpdated", notNullValue());
    }
}