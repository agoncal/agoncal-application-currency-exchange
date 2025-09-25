package org.agoncal.application.currencyexchange.portfolio.rates;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ApplicationScoped
public class RatesService {

    double fluctuationFactor = 0.2d;

    private static final Map<String, BigDecimal> BASE_RATES = Map.of(
        "AUD", BigDecimal.valueOf(1.5234),
        "CAD", BigDecimal.valueOf(1.3425),
        "CHF", BigDecimal.valueOf(0.9156),
        "EUR", BigDecimal.valueOf(0.9217),
        "GBP", BigDecimal.valueOf(0.7905),
        "JPY", BigDecimal.valueOf(149.25)
    );

    /**
     * Currency-specific seeds used in the rate fluctuation algorithm.
     * Each currency gets a unique seed value that is added to the current timestamp
     * in the sin() function to create different fluctuation patterns for each currency.
     * This ensures that different currencies don't fluctuate in sync and creates
     * more realistic, independent exchange rate movements.
     */
    private static final Map<String, Long> CURRENCY_SEEDS = Map.of(
        "AUD", 1000L,
        "CAD", 2000L,
        "CHF", 3000L,
        "EUR", 4000L,
        "GBP", 5000L,
        "JPY", 6000L
    );

    public List<ExchangeRate> getAllCurrentRates() {
        return Stream.of("AUD", "CAD", "CHF", "EUR", "GBP", "JPY")
            .map(currency -> calculateRate(currency, LocalDateTime.now()))
            .toList();
    }

    private ExchangeRate calculateRate(String currencyCode, LocalDateTime timestamp) {
        BigDecimal baseRate = BASE_RATES.get(currencyCode);
        if (baseRate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currencyCode);
        }

        // Generate fluctuation using current timestamp + currency-specific seed
        long seed = CURRENCY_SEEDS.get(currencyCode);
        long currentTime = System.currentTimeMillis() / 1_000; // Convert to seconds
        double fluctuation = Math.sin(currentTime + seed) * fluctuationFactor;

        BigDecimal rate = baseRate.add(BigDecimal.valueOf(fluctuation))
            .setScale(4, RoundingMode.HALF_UP);

        // Ensure JPY has appropriate scale (2 decimal places)
        if ("JPY".equals(currencyCode)) {
            rate = rate.setScale(2, RoundingMode.HALF_UP);
        }

        return new ExchangeRate(currencyCode, rate, timestamp);
    }
}