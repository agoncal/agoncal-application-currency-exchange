package org.agoncal.application.currencyexchange.trades;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

@Path("/api/trades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TradeResource {

    private static final Logger LOG = Logger.getLogger(TradeResource.class);

    @Inject
    TradeService tradeService;

    @POST
    public Response executeTrade(Trade trade) {
        LOG.info("Executing trade for user: " + trade.userId + ", currency: " + trade.toCurrency + ", amount: " + trade.usdAmount);

        try {
            Trade executedTrade = tradeService.executeTrade(trade);
            LOG.info("Trade executed successfully with status: " + executedTrade.status);
            return Response.ok(executedTrade).build();
        } catch (IllegalArgumentException e) {
            LOG.warn("Invalid trade request: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("Error executing trade", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Internal server error"))
                .build();
        }
    }

    @GET
    @Path("/{userId}")
    public Response getAllTrades(@PathParam("userId") String userId) {
        LOG.info("Getting trade history for user: " + userId);

        try {
            List<Trade> trades = tradeService.getAllTrades(userId);
            LOG.info("Returning " + trades.size() + " trades for user: " + userId);
            return Response.ok(trades).build();
        } catch (IllegalArgumentException e) {
            LOG.warn("Invalid request: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("Error getting trade history for user: " + userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Internal server error"))
                .build();
        }
    }
}