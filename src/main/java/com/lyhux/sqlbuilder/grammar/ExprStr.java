package com.lyhux.sqlbuilder.grammar;

public sealed interface ExprStr
    permits RawStr, EscapedStr {
    public String getValue();
}
