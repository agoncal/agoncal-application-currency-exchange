package org.agoncal.application.currencyexchange.rates;

public record Currency(
    String code,    // EUR, GBP, JPY, CHF, CAD, AUD
    String name,    // Euro, British Pound, Japanese Yen
    String symbol   // €, £, ¥
) {}