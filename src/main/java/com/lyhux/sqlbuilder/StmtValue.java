package main.java.com.lyhux.sqlbuilder;

import java.sql.JDBCType;

public record StmtValue<T>(CustomType type, T value) {

}
