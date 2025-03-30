package com.lyhux.mybatiscrud.builder.grammar;

public sealed interface TableFactorExpr extends Expr
permits TableNameExpr, TableSubExpr {
}
