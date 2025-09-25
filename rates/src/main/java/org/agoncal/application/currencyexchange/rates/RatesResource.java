package org.agoncal.application.currencyexchange.rates;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/rates")
@Produces(MediaType.APPLICATION_JSON)
public class RatesResource {

    @Inject
    RatesService ratesService;

    @GET
    @Path("/currencies")
    public List<Currency> getCurrencies() {
        return ratesService.getSupportedCurrencies();
    }

    @GET
    public List<ExchangeRate> getAllCurrentRates() {
        return ratesService.getAllCurrentRates();
    }

    @GET
    @Path("/{to}")
    public Response getSpecificRate(@PathParam("to") String toCurrency) {
        ExchangeRate rate = ratesService.getCurrentRate(toCurrency.toUpperCase());

        if (rate == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Unsupported currency: " + toCurrency + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        }

        return Response.ok(rate).build();
    }
}
