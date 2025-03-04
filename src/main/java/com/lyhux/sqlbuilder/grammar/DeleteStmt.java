package com.lyhux.sqlbuilder.grammar;

public final class DeleteStmt implements Stmt {
    TableRefExpr tableRef;
    WhereExpr whereExpr;
    OrderByExpr orderBy;
    LimitExpr limit;

    public DeleteStmt(String table) {
        this(table, "");
    }

    public DeleteStmt(String table, String alias) {
        this.tableRef = new TableRefExpr(
                new TableNameExpr(
                        new EscapedStr(table),
                        new EscapedStr(alias))
        );
        this.whereExpr = new WhereExpr();
        this.orderBy = new OrderByExpr();
        this.limit = null;
    }

    public TableRefExpr getTableRef() { return tableRef; }
    public WhereExpr getWhereExpr() { return whereExpr; }
    public OrderByExpr getOrderBy() { return orderBy; }
    public LimitExpr getLimit() { return limit; }

    public DeleteStmt where(WhereNest nest) {
        nest.where(whereExpr);
        return this;
    }

    public DeleteStmt orderBy(String columns, String order) {
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
