package org.agoncal.application.currencyexchange.trades;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@ApplicationScoped
public class TradeService {

    private final Map<String, List<Trade>> tradeHistory = new HashMap<>();
    private final Random random = new Random();

    public Trade executeTrade(Trade trade) {
        // Input validation
        if (trade.userId == null || trade.userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (trade.toCurrency == null || trade.toCurrency.isBlank()) {
            throw new IllegalArgumentException("Target currency is required");
        }
        if (trade.exchangeRate == null || trade.exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
        if (trade.usdAmount == null || trade.usdAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("USD amount must be positive");
        }

        // Set timestamp if not already set
        if (trade.timestamp == null) {
            trade.timestamp = java.time.LocalDateTime.now();
        }

        // Calculate converted amount
        trade.convertedAmount = trade.usdAmount.multiply(trade.exchangeRate);

        // Randomly assign status (mostly COMPLETED, but some FAILED with lower weight)
        double statusRandom = random.nextDouble();
        if (statusRandom < 0.85) {
            trade.status = TradeStatus.COMPLETED;
        } else if (statusRandom < 0.95) {
            trade.status = TradeStatus.PENDING;
        } else {
            trade.status = TradeStatus.FAILED;
        }

        // Store trade in history
        tradeHistory.computeIfAbsent(trade.userId, k -> new ArrayList<>()).add(trade);

        return trade;
    }

    public List<Trade> getAllTrades(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        return tradeHistory.getOrDefault(userId, new ArrayList<>());
    }
}