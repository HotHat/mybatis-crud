package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.Expr;

import java.util.ArrayList;
import java.util.List;

public class TableRefsExpr implements Expr {
    List<TableRefExpr> tableRefs;

    public TableRefsExpr() {
        tableRefs = new ArrayList<TableRefExpr>();
    }

    public boolean isEmpty() { return tableRefs.isEmpty(); }

    public TableRefsExpr(TableRefExpr ref) {
        tableRefs = new ArrayList<>();
        tableRefs.add(ref);
    }

    public TableRefsExpr add(TableRefExpr ref) {
        this.tableRefs.add(ref);
        return this;
    }

    public List<TableRefExpr> getTableRefs() { return this.tableRefs; }

}
