package com.bobocode.bibernate.session;

public record EntityKey(Class<?> entityType, Object id) {
}
