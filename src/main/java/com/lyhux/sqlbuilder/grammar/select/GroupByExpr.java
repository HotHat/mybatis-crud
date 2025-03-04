package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.Expr;
import com.lyhux.sqlbuilder.grammar.ExprStr;
import com.lyhux.sqlbuilder.grammar.WhereNest;

import java.util.ArrayList;
import java.util.List;

public final class GroupByExpr implements Expr {
    List<ExprStr> columns;


    HavingExpr having;

    public GroupByExpr() {
        columns = new ArrayList<ExprStr>();
        having = new HavingExpr();
    }

    public boolean isEmpty() { return columns.isEmpty(); }

    public List<ExprStr> getColumns() { return columns; }

    public HavingExpr getHaving() { return having; }


    public GroupByExpr groupBy(ExprStr column) {
        this.columns.add(column);
        // this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public GroupByExpr having(WhereNest query) {
        query.where(having.getExpr());
        return this;
    }

    public GroupByExpr groupBy(ExprStr column, WhereNest query) {
        this.columns.add(column);
        this.having(query);
        return this;
    }
}
