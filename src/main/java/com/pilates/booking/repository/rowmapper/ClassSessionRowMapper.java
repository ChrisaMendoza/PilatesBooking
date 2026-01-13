package com.pilates.booking.repository.rowmapper;

import com.pilates.booking.domain.ClassSession;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ClassSession}, with proper type conversions.
 */
@Service
public class ClassSessionRowMapper implements BiFunction<Row, String, ClassSession> {

    private final ColumnConverter converter;

    public ClassSessionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ClassSession} stored in the database.
     */
    @Override
    public ClassSession apply(Row row, String prefix) {
        ClassSession entity = new ClassSession();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCoachName(converter.fromRow(row, prefix + "_coach_name", String.class));
        entity.setStartAt(converter.fromRow(row, prefix + "_start_at", ZonedDateTime.class));
        entity.setEndAt(converter.fromRow(row, prefix + "_end_at", ZonedDateTime.class));
        entity.setCapacity(converter.fromRow(row, prefix + "_capacity", Integer.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setStudioId(converter.fromRow(row, prefix + "_studio_id", Long.class));
        entity.setClassTypeId(converter.fromRow(row, prefix + "_class_type_id", Long.class));
        return entity;
    }
}
