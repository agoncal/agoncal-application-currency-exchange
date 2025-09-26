package org.agoncal.application.currencyexchange.trades;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trade {
    public String userId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDateTime timestamp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public TradeStatus status;     // PENDING, COMPLETED, FAILED

    public BigDecimal usdAmount;   // Original USD amount
    public String toCurrency;      // Target currency

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal convertedAmount;  // Amount received in target currency

    public BigDecimal exchangeRate;     // Rate used for conversion

    public Trade() {
    }

    public Trade(String userId, String toCurrency, BigDecimal exchangeRate, BigDecimal usdAmount) {
        this.userId = userId;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
        this.usdAmount = usdAmount;
        this.timestamp = LocalDateTime.now();
        this.status = TradeStatus.PENDING;
    }
}