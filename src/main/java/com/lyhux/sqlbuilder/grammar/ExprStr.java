package com.lyhux.sqlbuilder.grammar;

public sealed interface ExprStr
    permits RawStr, EscapedStr {
    public default boolean isRaw() { return false; }
    public default boolean isEscape() { return false; }
}
