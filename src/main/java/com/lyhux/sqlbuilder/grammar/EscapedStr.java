package com.lyhux.sqlbuilder.grammar;

public final class EscapedStr implements ExprStr {
    String value;

    public EscapedStr(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

    @Override
    public boolean isEscape() {
        return true;
    }
}
