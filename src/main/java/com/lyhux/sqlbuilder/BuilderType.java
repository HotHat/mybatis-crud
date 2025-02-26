package main.java.com.lyhux.sqlbuilder;

import java.sql.JDBCType;

public enum BuilderType {
    BUILDER;

    public static BuilderType ofType(final String name) {
        switch (name) {
            case "builder": return BUILDER;
            default: return null;
        }
    }
}