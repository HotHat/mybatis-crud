package main.java.com.lyhux.sqlbuilder;

public enum WhereJoin {
    AND, OR;

    public String getName() {
        return name().toUpperCase();
    }
}
