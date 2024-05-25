package com.bobocode.bibernate.session;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class SessionFactoryImpl implements SessionFactory {
    private final DataSource dataSource;

    public SessionFactoryImpl(String jdbcUrl, String username, String password) {
        var pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setURL(jdbcUrl);
        pgSimpleDataSource.setUser(username);
        pgSimpleDataSource.setPassword(password);
        this.dataSource = pgSimpleDataSource;
    }
    
    @Override
    public Session openSession() {
        return new SessionImpl(dataSource);
    }
}
