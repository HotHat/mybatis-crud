package com.lyhux.sqlbuilder.grammar;

import java.sql.JDBCType;

public record ExprValue<T>(JDBCType type, T value) {

    public static ExprValue<String> of(String val) {
        return new ExprValue<String>(JDBCType.VARCHAR, val);
    }
}
