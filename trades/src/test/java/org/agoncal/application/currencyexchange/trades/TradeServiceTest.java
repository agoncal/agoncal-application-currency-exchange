package org.agoncal.application.currencyexchange.trades;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TradeServiceTest {

    @Inject
    TradeService service;

    @Test
    void testExecuteTrade() {
        Trade request = new Trade();
        request.userId = "user123";
        request.toCurrency = "EUR";
        request.exchangeRate = new BigDecimal("0.9217");
        request.usdAmount = new BigDecimal("100.00");

        Trade result = service.executeTrade(request);
        assertEquals(request.userId, result.userId);
        assertEquals(request.toCurrency, result.toCurrency);
        assertEquals(request.exchangeRate, result.exchangeRate);
        assertEquals(request.usdAmount, result.usdAmount);
        assertNotNull(request.status);
        assertNotNull(request.timestamp);
    }

    @Test
    void testExecuteTradeInvalidData() {
        Trade request = new Trade();
        request.userId = "user123";
        request.toCurrency = "EUR";
        request.exchangeRate = new BigDecimal("-1.0"); // Invalid negative rate
        request.usdAmount = new BigDecimal("100.00");

        assertThrows(ConstraintViolationException.class, () -> {
            service.executeTrade(request);
        }, "Should throw ConstraintViolationException for invalid trade data");
    }

    @Test
    void testGetAllTradesEmpty() {
        List<Trade> result = service.getAllTrades("newuser");
        assertEquals(0, result.size());
    }

    @Test
    void testExecuteAndGetTrades() {
        // First, execute a trade
        Trade request = new Trade();
        request.userId = "user789";
        request.toCurrency = "EUR";
        request.exchangeRate = new BigDecimal("0.9217");
        request.usdAmount = new BigDecimal("100.00");

        Trade result = service.executeTrade(request);
        assertEquals(request.userId, result.userId);
        assertEquals(request.toCurrency, result.toCurrency);
        assertEquals(request.exchangeRate, result.exchangeRate);
        assertEquals(request.usdAmount, result.usdAmount);
        assertNotNull(request.status);
        assertNotNull(request.timestamp);

        // Then retrieve trades for the user
        List<Trade> history = service.getAllTrades("user789");
        assertEquals(1, history.size());
    }
}