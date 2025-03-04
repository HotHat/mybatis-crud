package com.lyhux.sqlbuilder.grammar;

public sealed interface TableFactorExpr extends Expr
permits TableNameExpr, TableSubExpr {
}
