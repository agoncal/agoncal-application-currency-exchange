package org.agoncal.application.currencyexchange.portfolio.web;

import io.quarkus.qute.TemplateGlobal;
import io.quarkus.arc.Arc;
import org.agoncal.application.currencyexchange.portfolio.User;

public class TemplateGlobals {

    @TemplateGlobal
    public static User user() {
        UserSession userSession = Arc.container().instance(UserSession.class).get();
        return userSession.getCurrentUser();
    }

    @TemplateGlobal
    public static boolean isLoggedIn() {
        UserSession userSession = Arc.container().instance(UserSession.class).get();
        return userSession.isLoggedIn();
    }
}