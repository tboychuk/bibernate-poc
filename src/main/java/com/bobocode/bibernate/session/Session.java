package com.bobocode.bibernate.session;

public interface Session {

    <T> T findById(Class<T> entityType, Object id);

    void close();
}
