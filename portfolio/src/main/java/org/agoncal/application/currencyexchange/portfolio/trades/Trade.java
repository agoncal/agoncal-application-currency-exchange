package org.agoncal.application.currencyexchange.portfolio.trades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Trade(

    String userId,
    LocalDateTime timestamp,
    TradeStatus status,
    BigDecimal usdAmount,
    String toCurrency,
    BigDecimal convertedAmount,
    BigDecimal exchangeRate
) {

    public enum TradeStatus {
        PENDING,
        COMPLETED,
        FAILED
    }

    // Constructor for creating new trades (before execution)
    public Trade(String userId, BigDecimal usdAmount, String toCurrency, BigDecimal exchangeRate) {
        this(userId, LocalDateTime.now(), TradeStatus.PENDING, usdAmount, toCurrency, null, exchangeRate);
    }

    public Trade(String userId, TradeStatus status, BigDecimal usdAmount, String toCurrency, BigDecimal convertedAmount, BigDecimal exchangeRate) {
        this(userId, LocalDateTime.now(), status, usdAmount, toCurrency, convertedAmount, exchangeRate);
    }
}

