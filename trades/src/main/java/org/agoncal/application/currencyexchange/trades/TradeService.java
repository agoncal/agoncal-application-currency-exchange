package org.agoncal.application.currencyexchange.trades;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@ApplicationScoped
public class TradeService {

    private final Map<String, List<Trade>> tradeHistory = new HashMap<>();
    private final Random random = new Random();

    public Trade executeTrade(@Valid Trade trade) {
        // Calculate converted amount
        BigDecimal convertedAmount = trade.usdAmount().multiply(trade.exchangeRate());

        // Randomly assign status (mostly COMPLETED, but some FAILED with lower weight)
        double statusRandom = random.nextDouble();
        TradeStatus status;
        if (statusRandom < 0.85) {
            status = TradeStatus.COMPLETED;
        } else if (statusRandom < 0.95) {
            status = TradeStatus.PENDING;
        } else {
            status = TradeStatus.FAILED;
        }

        // Create new trade with calculated values (records are immutable)
        Trade executedTrade = new Trade(
            trade.userId(),
            trade.timestamp() != null ? trade.timestamp() : LocalDateTime.now(),
            status,
            trade.usdAmount(),
            trade.toCurrency(),
            convertedAmount,
            trade.exchangeRate()
        );

        // Store trade in history
        tradeHistory.computeIfAbsent(trade.userId(), k -> new ArrayList<>()).add(executedTrade);

        return executedTrade;
    }

    public List<Trade> getAllTrades(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        return tradeHistory.getOrDefault(userId, new ArrayList<>());
    }
}