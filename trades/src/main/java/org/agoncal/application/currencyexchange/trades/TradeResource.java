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

    @Inject
    TradeService tradeService;

    @POST
    public void executeTrade(Trade trade) {
        tradeService.executeTrade(trade);
    }

    @GET
    @Path("/{userId}")
    public Response getAllTrades(@PathParam("userId") String userId) {
        return Response.ok(tradeService.getAllTrades(userId)).build();
    }
}