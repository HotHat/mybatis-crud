package com.lyhux.mybatiscrud.builder.grammar;

public record DeleteStmt(
    TableRefsExpr table,
    WhereExpr where,
    OrderByExpr orderBy,
    LimitExpr limit
) implements Stmt {

}
