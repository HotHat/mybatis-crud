package com.lyhux.mybatiscrud.builder.grammar;

public final class EscapedStr implements ExprStr {
    String value;

    public EscapedStr(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

}
