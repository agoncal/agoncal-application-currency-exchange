package org.agoncal.application.currencyexchange.trade;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

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