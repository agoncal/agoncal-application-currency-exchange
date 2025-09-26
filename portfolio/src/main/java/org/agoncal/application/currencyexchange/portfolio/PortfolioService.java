package org.agoncal.application.currencyexchange.portfolio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.agoncal.application.currencyexchange.portfolio.rates.ExchangeRate;
import org.agoncal.application.currencyexchange.portfolio.rates.RatesService;

import java.util.List;

import static org.agoncal.application.currencyexchange.portfolio.User.USER_PORTFOLIOS;

@ApplicationScoped
public class PortfolioService {

    @Inject
    RatesService ratesService;

    public List<Portfolio> getUserPortfolio(String userId) {
        return USER_PORTFOLIOS.getOrDefault(userId, List.of());
    }

    public List<ExchangeRate> getAllCurrentRates() {
        return ratesService.getAllCurrentRates();
    }
}
