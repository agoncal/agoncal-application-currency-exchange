package org.agoncal.application.currencyexchange.currency;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@GrpcService
public class ExchangeRateServiceImpl extends ExchangeRateServiceGrpc.ExchangeRateServiceImplBase {

    private static final Logger LOG = Logger.getLogger(ExchangeRateServiceImpl.class);

    @Inject
    @ConfigProperty(name = "exchange-rates.fluctuation-factor", defaultValue = "0.02")
    double fluctuationFactor;

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

    @Override
    public void getAllCurrentRates(Empty request, StreamObserver<ExchangeRateListResponse> responseObserver) {
        LOG.info("Getting all current exchange rates");

        try {
            List<ExchangeRateData> rates = getAllCurrentRatesInternal();

            ExchangeRateListResponse.Builder responseBuilder = ExchangeRateListResponse.newBuilder();

            for (ExchangeRateData rate : rates) {
                ExchangeRate grpcRate = convertToGrpcExchangeRate(rate);
                responseBuilder.addRates(grpcRate);
            }

            ExchangeRateListResponse response = responseBuilder.build();
            LOG.info("Returning " + rates.size() + " exchange rates");

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting all current rates", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getCurrentRate(CurrencyRequest request, StreamObserver<ExchangeRateResponse> responseObserver) {
        LOG.info("Getting current rate for currency: " + request.getCurrencyCode());

        try {
            ExchangeRateData rate = getCurrentRateInternal(request.getCurrencyCode());

            ExchangeRate grpcRate = convertToGrpcExchangeRate(rate);

            ExchangeRateResponse response = ExchangeRateResponse.newBuilder()
                .setRate(grpcRate)
                .build();

            LOG.info("Returning exchange rate for " + request.getCurrencyCode() + ": " + rate.rate());

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting current rate for " + request.getCurrencyCode(), e);
            responseObserver.onError(e);
        }
    }

    public List<ExchangeRateData> getAllCurrentRatesInternal() {
        return ExchangeRateData.SUPPORTED_CURRENCIES.stream()
            .map(currencyCode -> calculateRate(currencyCode, LocalDateTime.now()))
            .toList();
    }

    public ExchangeRateData getCurrentRateInternal(String currencyCode) {
        if (!ExchangeRateData.SUPPORTED_CURRENCIES.contains(currencyCode)) {
            return null;
        }
        return calculateRate(currencyCode, LocalDateTime.now());
    }

    private ExchangeRateData calculateRate(String currencyCode, LocalDateTime timestamp) {
        BigDecimal baseRate = ExchangeRateData.EXCHANGE_RATES.get(currencyCode);
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

        return new ExchangeRateData(currencyCode, rate, timestamp);
    }

    private ExchangeRate convertToGrpcExchangeRate(ExchangeRateData javaRate) {
        return ExchangeRate.newBuilder()
            .setCurrency(javaRate.currency())
            .setRate(javaRate.rate().doubleValue())
            .setTimestamp(javaRate.timestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }
}