package org.agoncal.application.currencyexchange.portfolio;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Path("/api/portfolio")
@Produces(MediaType.APPLICATION_JSON)
public class PortfolioResource {

    // Hard-coded users
    private static final User USER1 = new User(1L, "John", "Doe", "john.doe@example.com");
    private static final User USER2 = new User(2L, "Jane", "Smith", "jane.smith@example.com");
    private static final User USER3 = new User(3L, "Bob", "Johnson", "bob.johnson@example.com");

    // Hard-coded portfolios for each user
    private static final Map<String, List<Portfolio>> USER_PORTFOLIOS = Map.of(
        "john.doe@example.com", List.of(
            new Portfolio(1L, USER1, "USD", BigDecimal.valueOf(1000.0), LocalDateTime.now()),
            new Portfolio(2L, USER1, "EUR", BigDecimal.valueOf(850.0), LocalDateTime.now()),
            new Portfolio(3L, USER1, "GBP", BigDecimal.valueOf(750.0), LocalDateTime.now()),
            new Portfolio(4L, USER1, "JPY", BigDecimal.valueOf(100000.0), LocalDateTime.now()),
            new Portfolio(5L, USER1, "CHF", BigDecimal.valueOf(900.0), LocalDateTime.now()),
            new Portfolio(6L, USER1, "CAD", BigDecimal.valueOf(1200.0), LocalDateTime.now()),
            new Portfolio(7L, USER1, "AUD", BigDecimal.valueOf(1100.0), LocalDateTime.now())
        ),
        "jane.smith@example.com", List.of(
            new Portfolio(8L, USER2, "USD", BigDecimal.valueOf(2000.0), LocalDateTime.now()),
            new Portfolio(9L, USER2, "EUR", BigDecimal.valueOf(1700.0), LocalDateTime.now()),
            new Portfolio(10L, USER2, "GBP", BigDecimal.valueOf(1500.0), LocalDateTime.now()),
            new Portfolio(11L, USER2, "JPY", BigDecimal.valueOf(200000.0), LocalDateTime.now()),
            new Portfolio(12L, USER2, "CHF", BigDecimal.valueOf(1800.0), LocalDateTime.now()),
            new Portfolio(13L, USER2, "CAD", BigDecimal.valueOf(2400.0), LocalDateTime.now()),
            new Portfolio(14L, USER2, "AUD", BigDecimal.valueOf(2200.0), LocalDateTime.now())
        ),
        "bob.johnson@example.com", List.of(
            new Portfolio(15L, USER3, "USD", BigDecimal.valueOf(500.0), LocalDateTime.now()),
            new Portfolio(16L, USER3, "EUR", BigDecimal.valueOf(425.0), LocalDateTime.now()),
            new Portfolio(17L, USER3, "GBP", BigDecimal.valueOf(375.0), LocalDateTime.now()),
            new Portfolio(18L, USER3, "JPY", BigDecimal.valueOf(50000.0), LocalDateTime.now()),
            new Portfolio(19L, USER3, "CHF", BigDecimal.valueOf(450.0), LocalDateTime.now()),
            new Portfolio(20L, USER3, "CAD", BigDecimal.valueOf(600.0), LocalDateTime.now()),
            new Portfolio(21L, USER3, "AUD", BigDecimal.valueOf(550.0), LocalDateTime.now())
        )
    );

    @GET
    @Path("/{userId}")
    public List<Portfolio> getUserPortfolio(@PathParam("userId") String userId) {
        return USER_PORTFOLIOS.getOrDefault(userId, List.of());
    }
}
