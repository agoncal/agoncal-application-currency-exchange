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
    void shouldExecuteTrade() {
        Trade trade = new Trade("user123", BigDecimal.valueOf(100), "EUR", BigDecimal.valueOf(0.92));
        service.executeTrade(trade);
    }

    @Test
    void shouldNotExecuteTradeInvalidData() {
        Trade trade = new Trade("", BigDecimal.valueOf(-10), "", BigDecimal.valueOf(-1));

        assertThrows(ConstraintViolationException.class, () -> {
            service.executeTrade(trade);
        }, "Should throw ConstraintViolationException for invalid trade data");
    }

    @Test
    void shouldGetAllTradesEmpty() {
        List<Trade> result = service.getAllTrades("newuser");
        assertEquals(0, result.size());
    }

    @Test
    void shouldExecuteAndGetTrades() {
        // First, execute a trade
        Trade trade = new Trade("user789", BigDecimal.valueOf(100), "EUR", BigDecimal.valueOf(0.92));

        service.executeTrade(trade);

        // Then retrieve trades for the user
        List<Trade> history = service.getAllTrades("user789");
        assertEquals(1, history.size());
    }
}