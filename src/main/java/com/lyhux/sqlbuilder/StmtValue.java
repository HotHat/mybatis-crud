package main.java.com.lyhux.sqlbuilder;

import java.sql.JDBCType;

public record StmtValue(JDBCType type, Object value) {

}
