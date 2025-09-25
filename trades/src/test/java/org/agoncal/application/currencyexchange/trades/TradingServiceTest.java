package org.agoncal.application.currencyexchange.trades;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TradingServiceTest {

    @GrpcClient
    TradingService tradingService;

    @Test
    public void testExecuteTradeSuccess() throws Exception {
        TradeRequest request = TradeRequest.newBuilder()
                .setUserId("user123")
                .setToCurrency("EUR")
                .setExchangeRate(0.85)
                .setUsdAmount(100.0)
                .build();

        CompletableFuture<TradeResponse> message = new CompletableFuture<>();
        tradingService.executeTrade(request).subscribe().with(
                reply -> message.complete(reply)
        );
        TradeResponse response = message.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertNotNull(response.getTradeId());
        assertEquals("EUR", response.getToCurrency());
        assertEquals(100.0, response.getUsdAmount(), 0.01);
        assertEquals(0.85, response.getExchangeRate(), 0.01);
        assertTrue(response.getConvertedAmount() > 0);
        assertNotNull(response.getTimestamp());

        // Status should be one of the valid enum values
        assertTrue(response.getStatus().equals("COMPLETED") ||
                  response.getStatus().equals("PENDING") ||
                  response.getStatus().equals("FAILED"));
    }

    @Test
    public void testExecuteTradeInvalidCurrency() throws Exception {
        TradeRequest request = TradeRequest.newBuilder()
                .setUserId("user123")
                .setToCurrency("INVALID")
                .setExchangeRate(0.85)
                .setUsdAmount(100.0)
                .build();

        CompletableFuture<TradeResponse> message = new CompletableFuture<>();
        tradingService.executeTrade(request).subscribe().with(
                reply -> message.complete(reply)
        );
        TradeResponse response = message.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals("FAILED", response.getStatus());
    }

    @Test
    public void testGetAllTradesEmpty() throws Exception {
        UserIdRequest request = UserIdRequest.newBuilder()
                .setUserId("newuser")
                .build();

        CompletableFuture<TradeListResponse> message = new CompletableFuture<>();
        tradingService.getAllTrades(request).subscribe().with(
                reply -> message.complete(reply)
        );
        TradeListResponse response = message.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals(0, response.getTradesList().size());
    }

    @Test
    public void testExecuteAndGetTrades() throws Exception {
        String userId = "testuser" + System.currentTimeMillis();

        // Execute a trade first
        TradeRequest executeRequest = TradeRequest.newBuilder()
                .setUserId(userId)
                .setToCurrency("EUR")
                .setExchangeRate(0.85)
                .setUsdAmount(100.0)
                .build();

        CompletableFuture<TradeResponse> executeMessage = new CompletableFuture<>();
        tradingService.executeTrade(executeRequest).subscribe().with(
                reply -> executeMessage.complete(reply)
        );
        TradeResponse executeResponse = executeMessage.get(5, TimeUnit.SECONDS);

        assertNotNull(executeResponse);
        assertNotNull(executeResponse.getTradeId());

        // Now get all trades for this user
        UserIdRequest getRequest = UserIdRequest.newBuilder()
                .setUserId(userId)
                .build();

        CompletableFuture<TradeListResponse> getMessage = new CompletableFuture<>();
        tradingService.getAllTrades(getRequest).subscribe().with(
                reply -> getMessage.complete(reply)
        );
        TradeListResponse getResponse = getMessage.get(5, TimeUnit.SECONDS);

        assertNotNull(getResponse);
        assertEquals(1, getResponse.getTradesList().size());

        TradeResponse storedTrade = getResponse.getTradesList().get(0);
        assertEquals(executeResponse.getTradeId(), storedTrade.getTradeId());
        assertEquals("EUR", storedTrade.getToCurrency());
    }
}