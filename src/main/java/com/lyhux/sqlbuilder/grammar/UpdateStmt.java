package com.lyhux.sqlbuilder.grammar;

import com.lyhux.sqlbuilder.grammar.update.AssignListExpr;
import com.lyhux.sqlbuilder.grammar.update.AssignNest;

public final class UpdateStmt implements Stmt{
    TableRefExpr tableRef;
    AssignListExpr assignments;
    WhereExpr whereExpr;
    OrderByExpr orderBy;
    LimitExpr limit;

    public UpdateStmt() {
        this("");
    }

    public UpdateStmt(String table) {
        this.tableRef = new TableRefExpr(
                        new TableNameExpr(
                                new EscapedStr(table),
                                new EscapedStr(""))
        );
        this.assignments = new AssignListExpr();
        this.whereExpr = new WhereExpr();
        this.orderBy = new OrderByExpr();
        this.limit = null;
    }

    public TableRefExpr getTableRef() { return tableRef; }
    public WhereExpr getWhereExpr() { return whereExpr; }
    public AssignListExpr getAssignments() { return assignments; }
    public OrderByExpr getOrderBy() { return orderBy; }
    public LimitExpr getLimit() { return limit; }


    public UpdateStmt table(String table) {
        this.tableRef = new TableRefExpr(
            new TableNameExpr(
                new EscapedStr(table),
                new EscapedStr(""))
        );
        return this;
    }

    public UpdateStmt where(WhereNest nest) {
        nest.where(whereExpr);
        return this;
    }
    public UpdateStmt set(AssignNest nest) {
        nest.updateSet(assignments);
        return this;
    }

    public UpdateStmt orderBy(String columns, String order) {
        orderBy.add(new OrderByItem(new EscapedStr(columns), order));
        return this;
    }

    public void limit(int count) {
        limit = new LimitExpr(count, 0);
    }

    public void limit(int count, int offset) {
        limit = new LimitExpr(count, offset);
    }

}
