package org.agoncal.application.currencyexchange.trades;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.math.BigDecimal;

class TradeTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validTradeShouldPassValidation() {
        Trade trade = new Trade();
        trade.userId = "user123";
        trade.usdAmount = BigDecimal.valueOf(100);
        trade.toCurrency = "EUR";
        trade.exchangeRate = BigDecimal.valueOf(0.92);
        trade.convertedAmount = BigDecimal.valueOf(92);
        // status and timestamp are set by default

        Set<ConstraintViolation<Trade>> violations = validator.validate(trade);
        assertTrue(violations.isEmpty(), "Valid trade should have no violations");
    }

    @Test
    void invalidTradeShouldFailValidation() {
        Trade trade = new Trade();
        trade.userId = ""; // NotBlank violation
        trade.usdAmount = BigDecimal.valueOf(-10); // DecimalMin violation
        trade.toCurrency = ""; // NotBlank violation
        trade.exchangeRate = BigDecimal.valueOf(-1); // DecimalMin violation
        trade.convertedAmount = BigDecimal.valueOf(-9);

        Set<ConstraintViolation<Trade>> violations = validator.validate(trade);
        assertFalse(violations.isEmpty(), "Invalid trade should have violations");
        // Optionally, check specific violations
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("usdAmount")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("toCurrency")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("exchangeRate")));
    }

    @Test
    void invalidTrade2ShouldFailValidation() {
        Trade trade = new Trade();
        trade.userId = "user123";
        trade.toCurrency = "EUR";
        trade.exchangeRate = new BigDecimal("-1.0"); // Invalid negative rate
        trade.usdAmount = new BigDecimal("100.00");

        Set<ConstraintViolation<Trade>> violations = validator.validate(trade);
        assertFalse(violations.isEmpty(), "Invalid trade should have violations");
        // Optionally, check specific violations
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("exchangeRate")));
    }
}