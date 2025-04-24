package com.lyhux.mybatiscrud.builder.grammar;

import java.sql.JDBCType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ExprResult (String statement, List<TypeValue<?>> bindings){

    public String toSql() {
        if (bindings == null || bindings.isEmpty()) return statement;

        StringBuilder sb = new StringBuilder(statement);
        for (TypeValue<?> binding : bindings) {
            int index = sb.indexOf("?");
            String replace = switch (binding.type()) {
                case JDBCType.NULL -> "null";
                case JDBCType.INTEGER, JDBCType.BIGINT, JDBCType.FLOAT, JDBCType.DOUBLE -> binding.value().toString();
                case JDBCType.TIMESTAMP -> {
                    var dateTime = binding.value();
                    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    if (dateTime instanceof Timestamp) {
                        yield "'" + ((Timestamp) dateTime).toLocalDateTime().format(formatter) + "'";
                    } else {
                        yield "'" + ((LocalDateTime) dateTime).format(formatter) + "'";
                    }
                }
                default -> "'" + binding.value() + "'";
            };

            sb.replace(index, index + 1, replace);
        }
        return sb.toString();
    }
}
