package org.agoncal.application.currencyexchange.rates;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class RatesResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/rates")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }

}