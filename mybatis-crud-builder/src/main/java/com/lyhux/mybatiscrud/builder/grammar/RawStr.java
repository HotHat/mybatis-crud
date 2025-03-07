package com.lyhux.mybatiscrud.builder.grammar;

public final class RawStr implements ExprStr {
    private final String value;

    public RawStr(String value) {
        this.value = value;
    }

    public String getValue() { return value; }
}
