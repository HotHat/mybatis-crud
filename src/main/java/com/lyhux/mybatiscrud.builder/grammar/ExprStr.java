package com.lyhux.mybatiscrud.builder.grammar;

public sealed interface ExprStr
    permits RawStr, EscapedStr {
    public String getValue();
}
