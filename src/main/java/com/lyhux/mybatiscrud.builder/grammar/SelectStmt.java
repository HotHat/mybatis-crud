package com.lyhux.mybatiscrud.builder.grammar;

import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.GroupByExpr;

public record SelectStmt(
    ColumnExpr select,
    TableRefsExpr tableRefs,
    WhereExpr where,
    GroupByExpr groupBy,
    OrderByExpr orderBy,
    LimitExpr limit,
    ForExpr forUpdate,
    UnionClause union
) implements Stmt {

}
