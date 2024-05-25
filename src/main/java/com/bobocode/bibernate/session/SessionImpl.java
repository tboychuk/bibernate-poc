package com.bobocode.bibernate.session;

import com.bobocode.bibernate.annotation.JoinColumn;
import com.bobocode.bibernate.annotation.OneToMany;
import com.bobocode.bibernate.collection.LazyList;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bobocode.bibernate.util.EntityUtils.*;

@RequiredArgsConstructor
public class SessionImpl implements Session {
    public static final String SELECT_FROM_TABLE_BY_COLUMN = "select * from %s where %s = ?";
    private final DataSource dataSource;
    private Map<EntityKey, Object> entitiesMap = new ConcurrentHashMap<>();
    private Map<EntityKey, Object[]> entitiesSnapshot = new ConcurrentHashMap<>();
    private boolean closed;

    @Override
    @SneakyThrows
    public <T> T findById(Class<T> entityType, Object id) {
        checkOpen();
        verifyEntity(entityType);
        // check in cache
        var entityKey = new EntityKey(entityType, id);
        var cachedEntity = entitiesMap.get(entityKey);
        if (cachedEntity != null) {
            return entityType.cast(cachedEntity);
        } else {
            // load from DB
            try (var connection = dataSource.getConnection()) {
                var tableName = resolveTableName(entityType);
                var idColumnName = resolveIdColumnName(entityType);
                var selectSql = SELECT_FROM_TABLE_BY_COLUMN.formatted(tableName, idColumnName);
                System.out.println("SQL: " + selectSql);
                try (var selectStatement = connection.prepareStatement(selectSql)) {
                    selectStatement.setObject(1, id);
                    var rs = selectStatement.executeQuery();
                    if (rs.next()) {
                        var entity = createEntityFromResultSet(entityKey, rs);
                        entitiesMap.put(entityKey, entity);
//                        System.out.println("Creating entity initialSnapshot");
                        var initialSnapshot = toSnapshot(entity);
                        entitiesSnapshot.put(entityKey, initialSnapshot);
//                        System.out.println(Arrays.toString(initialSnapshot));

                        return (T) entity;
                    }
                }
            }
            throw new RuntimeException("Entity not found by id =" + id);
        }
    }

    private void checkOpen() {
        if (closed) {
            throw new RuntimeException("Session is closed");
        }
    }

    @SneakyThrows
    public  <T> T createEntityFromResultSet(EntityKey entityKey, ResultSet rs) {
        var entityType = entityKey.entityType();
        var entity = entityType.getConstructor().newInstance();
        for (var field : entityType.getDeclaredFields()) {

            Object fieldValue = null;
            if (isRegularColumn(field)) {
                fieldValue = getFieldValue(field, rs);
            } else if (isEntity(field)) {
                var joinColumnName = resolveJoinColumnName(field);
                var relatedEntityId = rs.getObject(joinColumnName);
                var relatedEntityType = (Class<?>)field.getType();
                fieldValue = findById(relatedEntityType, relatedEntityId);
            } else if (isCollection(field)) {
                // todo: create a special list with lazy loading
                fieldValue = new LazyList<>(() -> findAllByField(entityKey, field));
            }

            field.setAccessible(true);
            field.set(entity, fieldValue);
        }
        return (T) entity;
    }

    @SneakyThrows
    private List<?> findAllByField(EntityKey entityKey, Field field)  {
        checkOpen();
        var parameterizedType = (ParameterizedType) field.getGenericType();
        var subEntityType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
        var mappedByFieldName = field.getAnnotation(OneToMany.class).mappedBy();

        var searchField = subEntityType.getDeclaredField(mappedByFieldName);
        var joinColumnName = searchField.getAnnotation(JoinColumn.class).value();
        var tableName = resolveTableName(subEntityType);
        var selectSql = SELECT_FROM_TABLE_BY_COLUMN.formatted(tableName, joinColumnName);
        System.out.println("SQL: "+selectSql);
        try (var connection = dataSource.getConnection()) {
            try (var selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setObject(1, entityKey.id());
                var rs = selectStatement.executeQuery();
                var list = new ArrayList<>();
                while (rs.next()) {
                    var subEntityIdColumnName = resolveIdColumnName(subEntityType);
                    var subEntityId = rs.getObject(subEntityIdColumnName);
                    var subEntityKey = new EntityKey(subEntityType, subEntityId);
                    var subEntity = createEntityFromResultSet(subEntityKey, rs);
                    list.add(subEntity);
                }
                return list;
            }
        }
    }


    @Override
    public void close() {
        dirtyChecking();
        this.entitiesMap.clear();
        this.entitiesSnapshot.clear();
        this.closed = true;
    }

    private void dirtyChecking() {
        for (var entityEntry : entitiesMap.entrySet()) {
            var entityKey = entityEntry.getKey();
            var entity = entityEntry.getValue();
            var currentSnapshot = toSnapshot(entity);
            var initialSnapshot = entitiesSnapshot.get(entityKey);
            if (isDirty(currentSnapshot, initialSnapshot)) {
                performUpdate(entityKey.id(), entity);
            }
        }
    }

    @SneakyThrows
    private void performUpdate(Object id, Object entity) {
        var entityType = entity.getClass();
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(entityType);
            var updateParams = resolveUpdateParams(entityType);
            var idColumnName = resolveIdColumnName(entityType);
            var updateSql = "update %s set %s where %s = ?".formatted(tableName, updateParams, idColumnName);
            System.out.println("SQL: " + updateSql);
            try (var updateStatement = connection.prepareStatement(updateSql)) {
                var updatableFields = getUpdatableFields(entityType);
                for (int i = 0; i < updatableFields.length; i++) {
                    var field = updatableFields[i];
                    var fieldValue = getFieldValueFromEntity(field, entity);
                    updateStatement.setObject(i+1, fieldValue);
                }
                updateStatement.setObject(updatableFields.length+1, id);
//                System.out.println("SQL: "+updateStatement);
                updateStatement.executeUpdate();
            }
        }
    }

  


}
