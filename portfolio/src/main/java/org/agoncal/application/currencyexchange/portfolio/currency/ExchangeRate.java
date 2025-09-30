package org.agoncal.application.currencyexchange.portfolio.currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRate(
    String currency,       // Target currency (EUR, GBP, JPY, etc.)
    BigDecimal rate,         // Exchange rate (1 USD = 0.9217 EUR)
    LocalDateTime timestamp  // When the rate was calculated
) {
}