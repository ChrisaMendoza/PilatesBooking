package com.pilates.booking.repository.rowmapper;

import com.pilates.booking.domain.Event;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Event}, with proper type conversions.
 */
@Service
public class EventRowMapper implements BiFunction<Row, String, Event> {

    private final ColumnConverter converter;

    public EventRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Event} stored in the database.
     */
    @Override
    public Event apply(Row row, String prefix) {
        Event entity = new Event();
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
