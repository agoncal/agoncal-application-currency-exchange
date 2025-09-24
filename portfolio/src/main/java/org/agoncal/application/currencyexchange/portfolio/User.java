package org.agoncal.application.currencyexchange.portfolio;

public record User(
    Long id,
    String name,
    String surname,
    String email
) {}