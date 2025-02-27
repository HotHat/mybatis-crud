package main.java.com.lyhux.sqlbuilder;

public enum BuilderType {
    BUILDER,
    INTEGER_ARRAY,
    STRING_ARRAY
    ;

    public static BuilderType ofType(final String name) {
        switch (name) {
            case "BUILDER": return BUILDER;
            case "INTEGER_ARRAY": return INTEGER_ARRAY;
            case "STRING_ARRAY": return STRING_ARRAY;
            default: return null;
        }
    }
}