package com.bobocode.bibernate;

import com.bobocode.bibernate.annotation.Column;
import com.bobocode.bibernate.annotation.Entity;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.annotation.Table;
import com.bobocode.demo.entity.Participant;
import com.google.common.base.CaseFormat;
import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Optional;

public class Orm {
    private final DataSource dataSource;

    public Orm(String jdbcUrl, String username, String password) {
        var pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setURL(jdbcUrl);
        pgSimpleDataSource.setUser(username);
        pgSimpleDataSource.setPassword(password);
        this.dataSource = pgSimpleDataSource;
    }

    @SneakyThrows
    public <T> T findById(Class<T> entityType, Object id) {
        verifyEntity(entityType);
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(entityType);
            var idColumnName = resolveIdColumnName(entityType);
            var selectSql = "select * from %s where %s = ?".formatted(tableName, idColumnName);
            System.out.println("SQL: " + selectSql);
            try (var selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setObject(1, id);
                var rs = selectStatement.executeQuery();
                
                if (rs.next()) {
                    return createEntityFromResultSet(entityType, rs);
//                    var entity = new Participant();
//                    entity.setFirstName(rs.getString("first_name"));
//                    entity.setLastName(rs.getString("last_name"));
//                    return (T) entity;
                }
            }
        }
        throw new RuntimeException("Entity not found by id =" + id);
    }

    private <T> T createEntityFromResultSet(Class<T> entityType, ResultSet rs) {
        // todo: create entity and set all fields using Reflection API
        return null;
    }

    private String resolveIdColumnName(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .map(this::resolveColumnName)
                .orElseThrow(() -> new RuntimeException("Entity " + entityType.getSimpleName() + " must have an @Id"));
    }

    private String resolveColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .orElseGet(() -> underscore(field.getName()));
    }

    private void verifyEntity(Class<?> entityType) {
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new RuntimeException(entityType.getSimpleName() + " is not an @Entity");
        }
    }

    private String resolveTableName(Class<?> entityType) {
        return Optional.ofNullable(entityType.getAnnotation(Table.class))
                .map(Table::value)
                .orElseGet(() -> underscore(entityType.getSimpleName()));
    }

    private String underscore(String value) {
        return CaseFormat.LOWER_CAMEL
                .converterTo(CaseFormat.LOWER_UNDERSCORE)
                .convert(value);
    }
}
