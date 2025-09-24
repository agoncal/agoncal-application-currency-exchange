package org.agoncal.application.currencyexchange.portfolio.web;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.agoncal.application.currencyexchange.portfolio.Portfolio;
import org.agoncal.application.currencyexchange.portfolio.PortfolioResource;
import org.agoncal.application.currencyexchange.portfolio.User;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

public class WebApplication extends Controller {

    private static final Logger LOG = Logger.getLogger(WebApplication.class);

    @Inject
    UserSession userSession;

    @CheckedTemplate
    static class Templates {
        public static native TemplateInstance index();
        public static native TemplateInstance signin(String loginError, String passwordError, String email);
        public static native TemplateInstance portfolio(User user, List<Portfolio> portfolios);
        public static native TemplateInstance profile(User user);
    }

    @Path("/")
    public TemplateInstance index() {
        LOG.info("Entering index()");
        return Templates.index();
    }

    @GET
    @Path("/signin")
    public TemplateInstance signinPage() {
        LOG.info("Entering signinPage()");
        return Templates.signin(null, null, null);
    }

    @POST
    @Path("/signin")
    public TemplateInstance signin(@RestForm String email, @RestForm String password) {
        LOG.info("Entering signin() for user: " + (email != null ? email.trim() : "null"));
        String loginError = null;
        String passwordError = null;

        if (email == null || email.trim().isEmpty()) {
            loginError = "Email is required";
            LOG.warn("Signin failed: Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            passwordError = "Password is required";
            LOG.warn("Signin failed: Password is required");
        }

        if (loginError != null || passwordError != null) {
            return Templates.signin(loginError, passwordError, email);
        }

        User user = findUserByEmail(email.trim());
        if (user == null) {
            loginError = "User not found";
            LOG.warn("Signin failed: User not found - " + email.trim());
            return Templates.signin(loginError, passwordError, email);
        }

        if (!"password".equals(password)) {
            passwordError = "Invalid password";
            LOG.warn("Signin failed: Invalid password for user - " + email.trim());
            return Templates.signin(loginError, passwordError, email);
        }

        userSession.setCurrentUser(user);
        LOG.info("Successful signin for user: " + user.email());

        return portfolio();
    }

    @Path("/logout")
    public TemplateInstance logout() {
        LOG.info("Entering logout()");
        User currentUser = userSession.getCurrentUser();
        if (currentUser != null) {
            LOG.info("User logout: " + currentUser.email());
        }
        userSession.logout();
        return index();
    }

    @Path("/portfolio")
    public TemplateInstance portfolio() {
        LOG.info("Entering portfolio()");
        if (!userSession.isLoggedIn()) {
            LOG.info("Portfolio access attempt without authentication - redirecting to signin");
            return signinPage();
        }

        User currentUser = userSession.getCurrentUser();
        List<Portfolio> portfolios = PortfolioResource.getUserPortfolio(currentUser.email());
        LOG.info("Viewing portfolio for user: " + currentUser.email() + " with " + portfolios.size() + " entries");

        return Templates.portfolio(currentUser, portfolios);
    }

    @Path("/profile")
    public TemplateInstance profile() {
        LOG.info("Entering profile()");
        if (!userSession.isLoggedIn()) {
            LOG.info("Profile access attempt without authentication - redirecting to signin");
            return signinPage();
        }
        User currentUser = userSession.getCurrentUser();
        LOG.info("Viewing profile for user: " + currentUser.email());
        return Templates.profile(currentUser);
    }

    private User findUserByEmail(String email) {
        return switch (email) {
            case "john.doe@example.com" -> new User(1L, "John", "Doe", "john.doe@example.com");
            case "jane.smith@example.com" -> new User(2L, "Jane", "Smith", "jane.smith@example.com");
            case "bob.johnson@example.com" -> new User(3L, "Bob", "Johnson", "bob.johnson@example.com");
            default -> null;
        };
    }
}