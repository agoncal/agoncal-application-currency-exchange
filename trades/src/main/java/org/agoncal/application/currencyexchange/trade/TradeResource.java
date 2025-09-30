package org.agoncal.application.currencyexchange.trade;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/trades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TradeResource {

    @Inject
    TradeService tradeService;

    @POST
    public void executeTrade(Trade trade) {
        tradeService.executeTrade(trade);
    }

    @GET
    @Path("/{userId}")
    public List<Trade> getAllTrades(@PathParam("userId") String userId) {
        return tradeService.getAllTrades(userId);
    }
}