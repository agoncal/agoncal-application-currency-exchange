package org.agoncal.application.currencyexchange.portfolio.trade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TradeService {

    private final Map<String, List<Trade>> tradeHistory = new HashMap<>();

    public void executeTrade(Trade trade) {
        // Calculate converted amount
        BigDecimal convertedAmount = trade.usdAmount().multiply(trade.exchangeRate());

        // Create new trade with converted amount
        Trade executedTrade = new Trade(trade.userId(), trade.usdAmount(), trade.toCurrency(), convertedAmount, trade.exchangeRate());

        // Store trade in history
        tradeHistory.computeIfAbsent(trade.userId(), k -> new ArrayList<>()).add(executedTrade);
    }

    public List<Trade> getAllTrades(@NotBlank String userId) {
        return tradeHistory.getOrDefault(userId, new ArrayList<>());
    }
}
