package com.lyhux.mybatiscrud.builder.grammar;

public sealed interface WhereClauseExpr extends Expr
    permits WhereExpr, BinaryExpr {
}
