package org.agoncal.application.currencyexchange.trade;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Trade(

    @NotBlank
    String userId,
    LocalDateTime timestamp,
    @DecimalMin("0")
    BigDecimal usdAmount,
    @NotBlank
    String toCurrency,
    BigDecimal convertedAmount,
    @DecimalMin("0")
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

