package com.campus.util;

/** Central place for the ServletContext / HttpSession attribute keys used across the app. */
public final class Attributes {

    public static final String DATA_SOURCE = "dataSource";
    public static final String SESSION_USER = "currentUser";
    public static final String CSRF_TOKEN = "csrfToken";

    private Attributes() {
    }
}
