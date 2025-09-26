package org.agoncal.application.currencyexchange.trades;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Trade(

    @NotBlank
    String userId,
    LocalDateTime timestamp,
    TradeStatus status,     // PENDING, COMPLETED, FAILED
    @DecimalMin("0")
    BigDecimal usdAmount,   // Original USD amount
    @NotBlank
    String toCurrency,      // Target currency
    BigDecimal convertedAmount,  // Amount received in target currency
    @DecimalMin("0")
    BigDecimal exchangeRate     // Rate used for conversion
) {

    // Constructor for creating new trades (before execution)
    public Trade(String userId, BigDecimal usdAmount, String toCurrency, BigDecimal exchangeRate) {
        this(userId, LocalDateTime.now(), TradeStatus.PENDING, usdAmount, toCurrency, null, exchangeRate);
    }

    public Trade(String userId, TradeStatus status, BigDecimal usdAmount, String toCurrency, BigDecimal convertedAmount, BigDecimal exchangeRate) {
        this(userId, LocalDateTime.now(), status, usdAmount, toCurrency, convertedAmount, exchangeRate);
    }
}

