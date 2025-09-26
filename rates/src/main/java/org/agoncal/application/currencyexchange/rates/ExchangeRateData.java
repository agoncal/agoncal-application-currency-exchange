package org.agoncal.application.currencyexchange.rates;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateData(
    String currency,         // Target currency code (EUR, GBP, JPY, etc.)
    BigDecimal rate,         // Exchange rate (1 USD = 0.9217 EUR)
    LocalDateTime timestamp  // When the rate was calculated
) {}