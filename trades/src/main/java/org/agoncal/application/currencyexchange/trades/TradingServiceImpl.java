package org.agoncal.application.currencyexchange.trades;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class TradingServiceImpl extends TradingServiceGrpc.TradingServiceImplBase {

    private static final Logger LOG = Logger.getLogger(TradingServiceImpl.class);

    // In-memory storage for trade history (HashMap with userId as key, List of TradeResponseData as value)
    private final Map<String, List<TradeResponseData>> tradeHistory = new ConcurrentHashMap<>();

    // Valid currency codes
    private static final Set<String> VALID_CURRENCIES = Set.of("EUR", "GBP", "JPY", "CHF", "CAD", "AUD");

    @Override
    public void executeTrade(TradeRequest request, StreamObserver<TradeResponse> responseObserver) {
        LOG.info("Executing trade for user: " + request.getUserId() + ", currency: " + request.getToCurrency() + ", amount: " + request.getUsdAmount());

        try {
            // Input validation
            ValidationResult validation = validateTradeRequest(request);
            if (!validation.isValid()) {
                TradeResponse response = createFailedTradeResponse(request, validation.getErrorMessage());
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            // Create trade response
            TradeResponseData tradeData = processTradeRequest(request);

            // Store in history
            storeTradeInHistory(request.getUserId(), tradeData);

            // Convert to gRPC response
            TradeResponse response = convertToGrpcResponse(tradeData);

            LOG.info("Trade executed successfully: " + tradeData.tradeId + " with status: " + tradeData.status);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error executing trade", e);
            TradeResponse response = createFailedTradeResponse(request, "Internal server error");
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getAllTrades(UserIdRequest request, StreamObserver<TradeListResponse> responseObserver) {
        LOG.info("Getting all trades for user: " + request.getUserId());

        try {
            List<TradeResponseData> userTrades = tradeHistory.getOrDefault(request.getUserId(), new ArrayList<>());

            TradeListResponse.Builder builder = TradeListResponse.newBuilder();

            for (TradeResponseData trade : userTrades) {
                TradeResponse grpcResponse = convertToGrpcResponse(trade);
                builder.addTrades(grpcResponse);
            }

            TradeListResponse response = builder.build();
            LOG.info("Returning " + userTrades.size() + " trades for user: " + request.getUserId());

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting trades for user: " + request.getUserId(), e);
            responseObserver.onError(e);
        }
    }

    private ValidationResult validateTradeRequest(TradeRequest request) {
        // Check user ID
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            return new ValidationResult(false, "User ID is required");
        }

        // Check currency code
        if (request.getToCurrency() == null || !VALID_CURRENCIES.contains(request.getToCurrency())) {
            return new ValidationResult(false, "Invalid currency code. Supported: " + VALID_CURRENCIES);
        }

        // Check exchange rate
        if (request.getExchangeRate() <= 0) {
            return new ValidationResult(false, "Exchange rate must be greater than 0");
        }

        // Check USD amount
        if (request.getUsdAmount() <= 0) {
            return new ValidationResult(false, "USD amount must be greater than 0");
        }

        return new ValidationResult(true, null);
    }

    private TradeResponseData processTradeRequest(TradeRequest request) {
        // Generate trade ID (timestamp + random number)
        long tradeId = System.currentTimeMillis() + new Random().nextInt(1000);

        // Calculate converted amount
        BigDecimal usdAmount = BigDecimal.valueOf(request.getUsdAmount());
        BigDecimal exchangeRate = BigDecimal.valueOf(request.getExchangeRate());
        BigDecimal convertedAmount = usdAmount.multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);

        // Determine trade status (mostly COMPLETED, but randomly assign others with lower weight)
        TradeStatus status = generateTradeStatus();

        LocalDateTime timestamp = LocalDateTime.now();

        return new TradeResponseData(
            tradeId,
            status,
            usdAmount,
            request.getToCurrency(),
            convertedAmount,
            exchangeRate,
            timestamp
        );
    }

    private TradeStatus generateTradeStatus() {
        Random random = new Random();
        int chance = random.nextInt(100);

        // 85% chance of COMPLETED, 10% chance of PENDING, 5% chance of FAILED
        if (chance < 85) {
            return TradeStatus.COMPLETED;
        } else if (chance < 95) {
            return TradeStatus.PENDING;
        } else {
            return TradeStatus.FAILED;
        }
    }

    private void storeTradeInHistory(String userId, TradeResponseData tradeData) {
        tradeHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(tradeData);
    }

    private TradeResponse convertToGrpcResponse(TradeResponseData tradeData) {
        return TradeResponse.newBuilder()
            .setTradeId(tradeData.tradeId)
            .setStatus(tradeData.status.name())
            .setUsdAmount(tradeData.usdAmount.doubleValue())
            .setToCurrency(tradeData.toCurrency)
            .setConvertedAmount(tradeData.convertedAmount.doubleValue())
            .setExchangeRate(tradeData.exchangeRate.doubleValue())
            .setTimestamp(tradeData.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }

    private TradeResponse createFailedTradeResponse(TradeRequest request, String errorMessage) {
        long tradeId = System.currentTimeMillis() + new Random().nextInt(1000);

        return TradeResponse.newBuilder()
            .setTradeId(tradeId)
            .setStatus(TradeStatus.FAILED.name())
            .setUsdAmount(request.getUsdAmount())
            .setToCurrency(request.getToCurrency())
            .setConvertedAmount(0.0)
            .setExchangeRate(request.getExchangeRate())
            .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }

    private static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}