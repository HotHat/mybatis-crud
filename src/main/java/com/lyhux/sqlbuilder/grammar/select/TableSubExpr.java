package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.SelectStmt;

public final class TableSubExpr implements TableFactorExpr {
    SelectStmt selectStmt;
    String alias;

    public TableSubExpr(SelectStmt selectStmt) {
        this.selectStmt = selectStmt;
        alias = "";
    }

    public TableSubExpr(final SelectStmt selectStmt, final String alias) {
        this.selectStmt = selectStmt;
        this.alias = alias;
    }

    public SelectStmt getSelectStmt() { return selectStmt; }
    public String getAlias() { return alias; }

}
