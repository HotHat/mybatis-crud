package main.java.com.lyhux.sqlbuilder;


import java.sql.JDBCType;

public class CustomType {
    private JDBCType jdbcType;
    private BuilderType builderType;
    private final boolean isJDBCType;

    public boolean isJDBCType() {
        return isJDBCType;
    }

    public CustomType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
        isJDBCType = true;
    }

    public CustomType(BuilderType builderType) {
        this.builderType = builderType;
        isJDBCType = false;
    }

    public BuilderType getBuilderType() {
        return builderType;
    }

    public JDBCType getJDBCType() {
        return jdbcType;
    }

    public static CustomType ofType(JDBCType jdbcType) {
        return new CustomType(jdbcType);
    }

    public static CustomType ofType(BuilderType builderType) {
        return new CustomType(builderType);
    }

    @Override
    public String toString() {
        return isJDBCType ? jdbcType.toString() : "BUILDER";
    }
}
