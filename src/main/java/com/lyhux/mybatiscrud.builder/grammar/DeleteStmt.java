package com.lyhux.mybatiscrud.builder.grammar;

public record DeleteStmt(
    TableRefExpr table,
    WhereExpr where,
    OrderByExpr orderBy,
    LimitExpr limit
) implements Stmt {

}
