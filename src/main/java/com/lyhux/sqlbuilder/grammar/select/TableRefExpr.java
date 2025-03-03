package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.Expr;

import java.util.ArrayList;
import java.util.List;

public class TableRefExpr implements Expr {
    TableFactorExpr tableFactor;
    List<TableJoinedExpr> joined;

    public TableRefExpr(TableFactorExpr tableFactor) {
        this.tableFactor = tableFactor;
        joined = new ArrayList<TableJoinedExpr>();
    }

    public TableRefExpr add(TableJoinedExpr joined) {
        this.joined.add(joined);
        return this;
    }

    public TableFactorExpr getTableFactor() { return tableFactor; }
    public List<TableJoinedExpr> getJoined() { return joined; }

}
