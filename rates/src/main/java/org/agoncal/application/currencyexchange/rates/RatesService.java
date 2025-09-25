package org.agoncal.application.currencyexchange.rates;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RatesService {

    @Inject
    @ConfigProperty(name = "exchange-rates.fluctuation-factor", defaultValue = "0.02")
    double fluctuationFactor;

    private static final Map<String, Currency> CURRENCIES = Map.of(
        "AUD", new Currency("AUD", "Australian Dollar", "A$"),
        "CAD", new Currency("CAD", "Canadian Dollar", "C$"),
        "CHF", new Currency("CHF", "Swiss Franc", "CHF"),
        "EUR", new Currency("EUR", "Euro", "€"),
        "GBP", new Currency("GBP", "British Pound", "£"),
        "JPY", new Currency("JPY", "Japanese Yen", "¥")
    );

    private static final Map<String, BigDecimal> EXCHANGE_RATES = Map.of(
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
        return CURRENCIES.values().stream()
            .map(currency -> calculateRate(currency, LocalDateTime.now()))
            .toList();
    }

    public ExchangeRate getCurrentRate(String currencyCode) {
        Currency currency = CURRENCIES.get(currencyCode);
        if (currency == null) {
            return null;
        }
        return calculateRate(currency, LocalDateTime.now());
    }

    private ExchangeRate calculateRate(Currency currency, LocalDateTime timestamp) {
        BigDecimal baseRate = EXCHANGE_RATES.get(currency.code());
        if (baseRate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currency.code());
        }

        // Generate fluctuation using current timestamp + currency-specific seed
        long seed = CURRENCY_SEEDS.get(currency.code());
        long currentTime = System.currentTimeMillis() / 1_000; // Convert to seconds
        double fluctuation = Math.sin(currentTime + seed) * fluctuationFactor;

        BigDecimal rate = baseRate.add(BigDecimal.valueOf(fluctuation))
            .setScale(4, RoundingMode.HALF_UP);

        // Ensure JPY has appropriate scale (2 decimal places)
        if ("JPY".equals(currency.code())) {
            rate = rate.setScale(2, RoundingMode.HALF_UP);
        }

        return new ExchangeRate(currency, rate, timestamp);
    }
}