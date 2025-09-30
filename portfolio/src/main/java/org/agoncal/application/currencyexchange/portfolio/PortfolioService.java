package org.agoncal.application.currencyexchange.portfolio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import static org.agoncal.application.currencyexchange.portfolio.User.USER_PORTFOLIOS;
import org.agoncal.application.currencyexchange.portfolio.currency.ExchangeRate;
import org.agoncal.application.currencyexchange.portfolio.currency.ExchangeRateService;
import org.agoncal.application.currencyexchange.portfolio.trade.Trade;
import org.agoncal.application.currencyexchange.portfolio.trade.TradeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class PortfolioService {

    @Inject
    ExchangeRateService exchangeRateService;

    @Inject
    TradeService tradeService;

    public List<Portfolio> getUserPortfolio(String userId) {
        return USER_PORTFOLIOS.getOrDefault(userId, List.of())
            .stream()
            .sorted(Comparator.comparing(Portfolio::currency))
            .toList();
    }

    public List<ExchangeRate> getAllCurrentRates() {
        return exchangeRateService.getAllCurrentRates();
    }

    public ExchangeRate getCurrentRate(String currencyCode) {
        return exchangeRateService.getCurrentRate(currencyCode);
    }

    public void executeTrade(Trade trade) {
        tradeService.executeTrade(trade);
        updateUserPortfolio(trade);
    }

    public List<Trade> getAllTrades(String userId) {
        return tradeService.getAllTrades(userId);
    }

    private static void updateUserPortfolio(Trade trade) {
        // Update user portfolio balance for the target currency
        List<Portfolio> userPortfolios = USER_PORTFOLIOS.get(trade.userId());
        if (userPortfolios != null) {
            // Calculate converted amount
            BigDecimal convertedAmount = trade.usdAmount().multiply(trade.exchangeRate());

            // Find the portfolio entry for the target currency
            Portfolio targetPortfolio = userPortfolios.stream()
                .filter(p -> p.currency().equals(trade.toCurrency()))
                .findFirst()
                .orElse(null);

            if (targetPortfolio != null) {
                // Update the balance by adding the converted amount (rounded to 1 decimal)
                BigDecimal newBalance = targetPortfolio.balance()
                    .add(convertedAmount)
                    .setScale(1, RoundingMode.HALF_UP);
                Portfolio updatedPortfolio = new Portfolio(
                    targetPortfolio.id(),
                    targetPortfolio.user(),
                    targetPortfolio.currency(),
                    newBalance,
                    java.time.LocalDateTime.now()
                );

                // Replace the old portfolio entry with the updated one
                userPortfolios.remove(targetPortfolio);
                userPortfolios.add(updatedPortfolio);
            }
        }
    }
}
