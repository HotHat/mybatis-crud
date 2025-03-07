package com.lyhux.mybatiscrud.builder.grammar;

public record LimitExpr(int rowCount, int offset) implements Expr {
    public LimitExpr(int rowCount) {
        this(rowCount, 0);
    }

}
