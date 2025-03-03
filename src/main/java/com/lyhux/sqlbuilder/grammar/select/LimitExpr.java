package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.Expr;

public record LimitExpr(int rowCount, int offset) implements Expr {
    public LimitExpr(int rowCount) {
        this(rowCount, 0);
    }

}
