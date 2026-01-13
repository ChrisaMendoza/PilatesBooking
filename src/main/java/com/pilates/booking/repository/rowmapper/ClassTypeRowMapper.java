package com.pilates.booking.repository.rowmapper;

import com.pilates.booking.domain.ClassType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ClassType}, with proper type conversions.
 */
@Service
public class ClassTypeRowMapper implements BiFunction<Row, String, ClassType> {

    private final ColumnConverter converter;

    public ClassTypeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ClassType} stored in the database.
     */
    @Override
    public ClassType apply(Row row, String prefix) {
        ClassType entity = new ClassType();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setDuration(converter.fromRow(row, prefix + "_duration", Integer.class));
        entity.setCapacity(converter.fromRow(row, prefix + "_capacity", Integer.class));
        return entity;
    }
}
