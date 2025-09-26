package org.agoncal.application.currencyexchange.portfolio.web;

import jakarta.enterprise.context.ApplicationScoped;
import org.agoncal.application.currencyexchange.portfolio.Portfolio;
import org.agoncal.application.currencyexchange.portfolio.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UserSession {

    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
    }
}