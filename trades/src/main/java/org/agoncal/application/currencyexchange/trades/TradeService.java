package org.agoncal.application.currencyexchange.trades;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@ApplicationScoped
public class TradeService {

    private static final Logger LOG = Logger.getLogger(TradeResource.class);

    private final Map<String, List<Trade>> tradeHistory = new HashMap<>();
    private final Random random = new Random();

    public void executeTrade(@Valid Trade trade) {
        LOG.info("Execute trade: " + trade);

        // Calculate converted amount
        BigDecimal convertedAmount = trade.usdAmount().multiply(trade.exchangeRate());

        // Create new trade with converted amount
        Trade executedTrade = new Trade(trade.userId(), getTradeStatus(), trade.usdAmount(), trade.toCurrency(), convertedAmount, trade.exchangeRate());

        // Store trade in history
        tradeHistory.computeIfAbsent(trade.userId(), k -> new ArrayList<>()).add(executedTrade);
    }

    public List<Trade> getAllTrades(@NotBlank String userId) {
        LOG.info("Getting trade history for user: " + userId);

        List<Trade> trades = tradeHistory.getOrDefault(userId, new ArrayList<>());

        LOG.info("Returning " + trades.size() + " trades for user: " + userId);
        return trades;
    }

    // Randomly assign status (mostly COMPLETED, but some FAILED with lower weight)
    private TradeStatus getTradeStatus() {
        double statusRandom = random.nextDouble();
        TradeStatus status;
        if (statusRandom < 0.85) {
            status = TradeStatus.COMPLETED;
        } else if (statusRandom < 0.95) {
            status = TradeStatus.PENDING;
        } else {
            status = TradeStatus.FAILED;
        }
        return status;
    }
}