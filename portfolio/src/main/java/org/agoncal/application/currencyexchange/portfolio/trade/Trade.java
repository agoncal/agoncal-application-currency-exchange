package org.agoncal.application.currencyexchange.portfolio.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Trade(

    String userId,
    LocalDateTime timestamp,
    BigDecimal usdAmount,
    String toCurrency,
    BigDecimal convertedAmount,
    BigDecimal exchangeRate
) {

    // Constructor for creating new trades (before execution)
    public Trade(String userId, BigDecimal usdAmount, String toCurrency, BigDecimal exchangeRate) {
        this(userId, LocalDateTime.now(), usdAmount, toCurrency, null, exchangeRate);
    }

    public Trade(String userId, BigDecimal usdAmount, String toCurrency, BigDecimal convertedAmount, BigDecimal exchangeRate) {
        this(userId, LocalDateTime.now(), usdAmount, toCurrency, convertedAmount, exchangeRate);
    }
}

