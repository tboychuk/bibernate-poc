package com.bobocode.bibernate.util;

import com.bobocode.bibernate.annotation.*;
import com.google.common.base.CaseFormat;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class EntityUtils {
    @SneakyThrows
    public static Object getFieldValue(Field field, ResultSet rs) {
        var columnName = resolveColumnName(field);
        var columnValue = rs.getObject(columnName);
        if (columnValue instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        return columnValue;
    }

    public static String resolveIdColumnName(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .map(EntityUtils::resolveColumnName)
                .orElseThrow(() -> new RuntimeException("Entity " + entityType.getSimpleName() + " must have an @Id"));
    }

    public static String resolveColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .orElseGet(() -> underscore(field.getName()));
    }

    public static void verifyEntity(Class<?> entityType) {
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new RuntimeException(entityType.getSimpleName() + " is not an @Entity");
        }
    }

    public static String resolveTableName(Class<?> entityType) {
        return Optional.ofNullable(entityType.getAnnotation(Table.class))
                .map(Table::value)
                .orElseGet(() -> underscore(entityType.getSimpleName()));
    }

    
    public static String resolveJoinColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(JoinColumn.class))
                .map(JoinColumn::value)
                .orElseGet(() -> underscore(field.getName() + "_id"));
    }

    public static boolean isRegularColumn(Field field) {
        return !isEntity(field) && !isCollection(field);
    }

    public static boolean isEntity(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

    public static boolean isCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    @SneakyThrows
    public static Object[] toSnapshot(Object entity) {
        return Arrays.stream(getUpdatableFields(entity.getClass()))
                .sorted(Comparator.comparing(Field::getName))
                .map(field -> getFieldValueFromEntity(field, entity))
                .toArray();
    }

    @SneakyThrows
    public static Object getFieldValueFromEntity(Field field, Object entity) {
        field.setAccessible(true);
        return field.get(entity);
    }

    public static String underscore(String value) {
        return CaseFormat.LOWER_CAMEL
                .converterTo(CaseFormat.LOWER_UNDERSCORE)
                .convert(value);
    }

    public static boolean isDirty(Object[] currentSnapshot, Object[] initialSnapshot) {
        for (int i = 0; i < currentSnapshot.length; i++) {
            if (!Objects.equals(currentSnapshot[i], initialSnapshot[i])) {
                return true;
            }
        }
        return false;
    }

    public static String resolveUpdateParams(Class<?> entityType) {
        return Arrays.stream(getUpdatableFields(entityType))
                .map(EntityUtils::resolveColumnName)
                .map(columnName -> columnName + " = ?")
                .collect(Collectors.joining(", "));

    }

    public static Field[] getUpdatableFields(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .sorted(Comparator.comparing(Field::getName))
                .filter(EntityUtils::isUpdatable)
                .toArray(Field[]::new);
    }

    public static boolean isUpdatable(Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            return false;
        }
        var columnAnnotation = field.getAnnotation(Column.class);
        return columnAnnotation == null || columnAnnotation.updatable();
    }
}
