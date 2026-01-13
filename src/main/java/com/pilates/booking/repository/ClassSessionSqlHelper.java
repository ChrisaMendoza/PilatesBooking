package com.pilates.booking.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ClassSessionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("coach_name", table, columnPrefix + "_coach_name"));
        columns.add(Column.aliased("start_at", table, columnPrefix + "_start_at"));
        columns.add(Column.aliased("end_at", table, columnPrefix + "_end_at"));
        columns.add(Column.aliased("capacity", table, columnPrefix + "_capacity"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));

        columns.add(Column.aliased("studio_id", table, columnPrefix + "_studio_id"));
        columns.add(Column.aliased("class_type_id", table, columnPrefix + "_class_type_id"));
        return columns;
    }
}
