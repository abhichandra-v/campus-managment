package com.campus.listener;

import com.campus.util.Attributes;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Resolves the container-managed {@code jdbc/campusdb} JNDI DataSource once at
 * application startup and publishes it as a ServletContext attribute so
 * controllers can hand it to services/DAOs without repeating the JNDI lookup
 * on every request.
 */
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource dataSource = (DataSource) envCtx.lookup("jdbc/campusdb");
            sce.getServletContext().setAttribute(Attributes.DATA_SOURCE, dataSource);
        } catch (NamingException e) {
            throw new IllegalStateException(
                    "Failed to resolve JNDI DataSource 'jdbc/campusdb'. Ensure META-INF/context.xml "
                            + "was deployed with the webapp and that the database is reachable.", e);
        }
    }
}
