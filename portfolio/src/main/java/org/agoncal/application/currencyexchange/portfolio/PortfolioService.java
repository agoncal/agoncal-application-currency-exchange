package org.agoncal.application.currencyexchange.portfolio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import org.agoncal.application.currencyexchange.portfolio.rates.ExchangeRate;
import org.agoncal.application.currencyexchange.portfolio.rates.ExchangeRateService;
import org.agoncal.application.currencyexchange.portfolio.trades.Trade;
import org.agoncal.application.currencyexchange.portfolio.trades.TradeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.agoncal.application.currencyexchange.portfolio.User.USER_PORTFOLIOS;

@ApplicationScoped
public class PortfolioService {

    @Inject
    ExchangeRateService exchangeRateService;

    @Inject
    TradeService tradeService;

    public List<Portfolio> getUserPortfolio(String userId) {
        return USER_PORTFOLIOS.getOrDefault(userId, List.of());
    }

    public List<ExchangeRate> getAllCurrentRates() {
        return exchangeRateService.getAllCurrentRates();
    }

    public ExchangeRate getCurrentRate(String currencyCode) {
        return exchangeRateService.getCurrentRate(currencyCode);
    }

    public void executeTrade(Trade trade) {
        tradeService.executeTrade(trade);
    }

    public List<Trade> getAllTrades(String userId) {
        return tradeService.getAllTrades(userId);
    }
}
