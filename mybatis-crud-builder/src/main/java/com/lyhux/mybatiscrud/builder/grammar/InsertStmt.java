package com.lyhux.mybatiscrud.builder.grammar;

import com.lyhux.mybatiscrud.builder.grammar.insert.*;

public record InsertStmt(
    TableRefExpr tableRef,
    ColumnExpr columns,
    ValuesExpr values,
    DuplicateAssignListExpr assigns
) implements Stmt {

}
