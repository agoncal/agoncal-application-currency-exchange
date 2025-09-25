package org.agoncal.application.currencyexchange.trades;

import java.math.BigDecimal;

public class TradeRequestData {
    public String userId;
    public String toCurrency;      // EUR, GBP, JPY, etc.
    public BigDecimal exchangeRate;     // Rate used for conversion
    public BigDecimal usdAmount;   // Amount in USD to convert

    public TradeRequestData() {}

    public TradeRequestData(String userId, String toCurrency, BigDecimal exchangeRate, BigDecimal usdAmount) {
        this.userId = userId;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
        this.usdAmount = usdAmount;
    }
}