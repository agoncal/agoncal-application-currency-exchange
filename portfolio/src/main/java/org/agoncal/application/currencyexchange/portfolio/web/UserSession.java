package org.agoncal.application.currencyexchange.portfolio.web;

import jakarta.enterprise.context.ApplicationScoped;
import org.agoncal.application.currencyexchange.portfolio.User;

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