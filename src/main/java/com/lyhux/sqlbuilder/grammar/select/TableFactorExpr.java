package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.Expr;

public sealed interface TableFactorExpr extends Expr
permits TableNameExpr, TableSubExpr {
}
