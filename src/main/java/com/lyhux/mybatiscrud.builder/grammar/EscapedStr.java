package com.lyhux.mybatiscrud.builder.grammar;

public final class EscapedStr implements ExprStr {
    String value;

    boolean isTableName;

    public EscapedStr(String value) {
        this(value, false);
    }

    public EscapedStr(String value, boolean isTableName) {
        this.value = value;
        this.isTableName = isTableName;
    }

    public String getValue() { return value; }
    public boolean isTableName() { return isTableName; }

}
