package main.java.com.lyhux.sqlbuilder.grammar;

import java.util.Arrays;

public final class SelectStmt implements Stmt {
    SelectExpr selectExpr;
    TableRefsExpr tableRefsExpr;
    WhereExpr whereExpr;

    public SelectStmt() {
        selectExpr = new SelectExpr();
        tableRefsExpr = new TableRefsExpr();
        whereExpr = new WhereExpr();
    }

    public SelectExpr getSelectExpr() { return selectExpr; }
    public TableRefsExpr getTableRefsExpr() { return tableRefsExpr; }
    public WhereExpr getWhereExpr() { return whereExpr; }

    public SelectStmt select(String... fields) {
        selectExpr.addAll(Arrays.stream(fields).map(EscapedStr::new).toList());
        return this;
    }

    public SelectStmt selectRaw(String field) {
        selectExpr.add(new RawStr(field));
        return this;
    }

    public SelectStmt from(String table) {
       return  from(table, "");
    }

    public SelectStmt from(SelectStmt table, String alias) {
        tableRefsExpr.add(new TableRefExpr(new TableSubExpr(table, alias)));
        return  this;
    }

    public SelectStmt from(String table, String alias) {
        tableRefsExpr.add(new TableRefExpr(new TableNameExpr(table, alias)));
        return this;
    }

    public SelectStmt where(WhereNest query) {
        whereExpr.where(query, true, "AND");
        return this;
    }

    public SelectStmt join(String table, String leftColumn, String operator, String rightColumn) {
        var tableExpr = new TableNameExpr(table);
        var whereExpr = new WhereExpr(false);
        whereExpr.on(leftColumn, operator, rightColumn);
        var joinedExpr = new TableJoinedExpr(tableExpr, whereExpr);
        assert !tableRefsExpr.isEmpty();
        var refs = tableRefsExpr.getTableRefs();

        var top = refs.getLast();
        top.add(joinedExpr);

        return this;
    }
}
