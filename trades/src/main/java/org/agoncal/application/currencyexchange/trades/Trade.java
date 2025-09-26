package org.agoncal.application.currencyexchange.trades;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trade {

    @NotBlank
    public String userId;
    public LocalDateTime timestamp = LocalDateTime.now();
    public TradeStatus status = TradeStatus.PENDING;     // PENDING, COMPLETED, FAILED
    @DecimalMin("0")
    public BigDecimal usdAmount;   // Original USD amount
    @NotBlank
    public String toCurrency;      // Target currency
    public BigDecimal convertedAmount;  // Amount received in target currency
    @DecimalMin("0")
    public BigDecimal exchangeRate;     // Rate used for conversion

    public Trade() {
    }
}

