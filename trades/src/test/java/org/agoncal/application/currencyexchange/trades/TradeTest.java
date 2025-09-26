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
        Trade trade = new Trade("user123", BigDecimal.valueOf(100), "EUR", BigDecimal.valueOf(0.92));

        Set<ConstraintViolation<Trade>> violations = validator.validate(trade);
        assertTrue(violations.isEmpty(), "Valid trade should have no violations");
    }

    @Test
    void invalidTradeShouldFailValidation() {
        Trade trade = new Trade("", BigDecimal.valueOf(-10), "", BigDecimal.valueOf(-1));

        Set<ConstraintViolation<Trade>> violations = validator.validate(trade);
        assertFalse(violations.isEmpty(), "Invalid trade should have violations");
        // Optionally, check specific violations
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("usdAmount")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("toCurrency")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("exchangeRate")));
    }
}