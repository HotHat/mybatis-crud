package com.lyhux.mybatiscrud.builder.grammar;

import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;

public record UpdateStmt(
    TableRefsExpr table,
    AssignListExpr assignments,
    WhereExpr where,
    OrderByExpr orderBy,
    LimitExpr limit
) implements Stmt{

}
