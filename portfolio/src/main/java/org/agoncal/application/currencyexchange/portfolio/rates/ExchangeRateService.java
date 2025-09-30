package org.agoncal.application.currencyexchange.portfolio.rates;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@ApplicationScoped
public class ExchangeRateService {

    private static final Random random = new Random();
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

    private static final List<String> ALL_CURRENCIES = List.of("AUD", "CAD", "CHF", "EUR", "GBP", "JPY");

    public List<ExchangeRate> getAllCurrentRates() {
        return ALL_CURRENCIES.stream()
            .map(this::calculateRate)
            .toList();
    }

    public ExchangeRate getCurrentRate(String currencyCode) {
        return calculateRate(currencyCode);
    }

    private ExchangeRate calculateRate(String currencyCode) {
        BigDecimal baseRate = BASE_RATES.get(currencyCode);
        if (baseRate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currencyCode);
        }

        // Randomly decide whether to fluctuate this specific currency (each has independent 70% chance)
        BigDecimal rate;
        if (random.nextDouble() < 0.4) {
            // Generate fluctuation using current timestamp + currency-specific seed
            long seed = CURRENCY_SEEDS.get(currencyCode);
            long currentTime = System.currentTimeMillis() / 1_000; // Convert to seconds
            double fluctuation = Math.sin(currentTime + seed) * fluctuationFactor;

            rate = baseRate.add(BigDecimal.valueOf(fluctuation))
                .setScale(4, RoundingMode.HALF_UP);
        } else {
            // Return base rate without fluctuation
            rate = baseRate.setScale(4, RoundingMode.HALF_UP);
        }

        // Ensure JPY has appropriate scale (2 decimal places)
        if ("JPY".equals(currencyCode)) {
            rate = rate.setScale(2, RoundingMode.HALF_UP);
        }

        return new ExchangeRate(currencyCode, rate, LocalDateTime.now());
    }
}