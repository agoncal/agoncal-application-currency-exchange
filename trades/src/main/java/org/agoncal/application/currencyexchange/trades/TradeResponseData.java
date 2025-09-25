package org.agoncal.application.currencyexchange.trades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeResponseData {
    public Long tradeId;           // Generated unique trade ID
    public TradeStatus status;     // PENDING, COMPLETED, FAILED
    public BigDecimal usdAmount;   // Original USD amount
    public String toCurrency;      // Target currency
    public BigDecimal convertedAmount;  // Amount received in target currency
    public BigDecimal exchangeRate;     // Rate used for conversion
    public LocalDateTime timestamp;

    public TradeResponseData() {}

    public TradeResponseData(Long tradeId, TradeStatus status, BigDecimal usdAmount,
                            String toCurrency, BigDecimal convertedAmount,
                            BigDecimal exchangeRate, LocalDateTime timestamp) {
        this.tradeId = tradeId;
        this.status = status;
        this.usdAmount = usdAmount;
        this.toCurrency = toCurrency;
        this.convertedAmount = convertedAmount;
        this.exchangeRate = exchangeRate;
        this.timestamp = timestamp;
    }
}