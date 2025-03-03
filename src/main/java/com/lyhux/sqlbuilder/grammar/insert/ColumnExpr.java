package com.lyhux.sqlbuilder.grammar.insert;

import com.lyhux.sqlbuilder.grammar.EscapedStr;
import com.lyhux.sqlbuilder.grammar.Expr;

import java.util.ArrayList;
import java.util.List;

public class ColumnExpr implements Expr {
    List<EscapedStr> columns;

    public List<EscapedStr> getColumns() {
        return columns;
    }

    public boolean isEmpty() { return columns.isEmpty(); }

    public ColumnExpr add(EscapedStr expr) {
        columns.add(expr);
        return this;
    }

    public void addAll(List<EscapedStr> fields) {
        this.columns.addAll(fields);
    }

    public ColumnExpr() {
        columns = new ArrayList<EscapedStr>();
    }
}
