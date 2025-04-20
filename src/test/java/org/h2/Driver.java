package org.h2;

public abstract class Driver implements java.sql.Driver {
    @Override
    public boolean acceptsURL(String url) throws java.sql.SQLException {
        return url.startsWith("jdbc:h2:");
    }
}
