package org.agoncal.application.currencyexchange.rates;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ExchangeRateServiceTest {

    @GrpcClient
    ExchangeRateService exchangeRateService;

    @Test
    void testGetAllCurrentRates() throws Exception {
        Empty request = Empty.newBuilder().build();

        CompletableFuture<ExchangeRateListResponse> message = new CompletableFuture<>();
        exchangeRateService.getAllCurrentRates(request).subscribe().with(
                reply -> message.complete(reply)
        );

        ExchangeRateListResponse response = message.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals(6, response.getRatesCount());

        // Verify all expected currencies are present
        boolean hasEUR = response.getRatesList().stream()
                .anyMatch(rate -> "EUR".equals(rate.getCurrency()));
        boolean hasGBP = response.getRatesList().stream()
                .anyMatch(rate -> "GBP".equals(rate.getCurrency()));
        boolean hasJPY = response.getRatesList().stream()
                .anyMatch(rate -> "JPY".equals(rate.getCurrency()));
        boolean hasCHF = response.getRatesList().stream()
                .anyMatch(rate -> "CHF".equals(rate.getCurrency()));
        boolean hasCAD = response.getRatesList().stream()
                .anyMatch(rate -> "CAD".equals(rate.getCurrency()));
        boolean hasAUD = response.getRatesList().stream()
                .anyMatch(rate -> "AUD".equals(rate.getCurrency()));

        assertTrue(hasEUR && hasGBP && hasJPY && hasCHF && hasCAD && hasAUD);

        // Verify each rate has required fields
        for (ExchangeRate rate : response.getRatesList()) {
            assertNotNull(rate.getCurrency());
            assertFalse(rate.getCurrency().isEmpty());
            assertTrue(rate.getRate() > 0);
            assertNotNull(rate.getTimestamp());
        }
    }

    @Test
    void testGetCurrentRateEUR() throws Exception {
        CurrencyRequest request = CurrencyRequest.newBuilder()
                .setCurrencyCode("EUR")
                .build();

        CompletableFuture<ExchangeRateResponse> message = new CompletableFuture<>();
        exchangeRateService.getCurrentRate(request).subscribe().with(
                reply -> message.complete(reply)
        );

        ExchangeRateResponse response = message.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertNotNull(response.getRate());

        ExchangeRate rate = response.getRate();
        assertEquals("EUR", rate.getCurrency());
        assertTrue(rate.getRate() > 0);
        assertNotNull(rate.getTimestamp());
    }

    @Test
    void testGetCurrentRateGBP() throws Exception {
        CurrencyRequest request = CurrencyRequest.newBuilder()
                .setCurrencyCode("GBP")
                .build();

        CompletableFuture<ExchangeRateResponse> message = new CompletableFuture<>();
        exchangeRateService.getCurrentRate(request).subscribe().with(
                reply -> message.complete(reply)
        );

        ExchangeRateResponse response = message.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertNotNull(response.getRate());

        ExchangeRate rate = response.getRate();
        assertEquals("GBP", rate.getCurrency());
        assertTrue(rate.getRate() > 0);
        assertNotNull(rate.getTimestamp());
    }

    @Test
    void testGetCurrentRateJPY() throws Exception {
        CurrencyRequest request = CurrencyRequest.newBuilder()
                .setCurrencyCode("JPY")
                .build();

        CompletableFuture<ExchangeRateResponse> message = new CompletableFuture<>();
        exchangeRateService.getCurrentRate(request).subscribe().with(
                reply -> message.complete(reply)
        );

        ExchangeRateResponse response = message.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertNotNull(response.getRate());

        ExchangeRate rate = response.getRate();
        assertEquals("JPY", rate.getCurrency());
        assertTrue(rate.getRate() > 0);
        assertNotNull(rate.getTimestamp());
    }
}