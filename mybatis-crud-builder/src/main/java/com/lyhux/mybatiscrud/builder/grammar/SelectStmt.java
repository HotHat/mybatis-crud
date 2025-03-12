package com.lyhux.mybatiscrud.builder.grammar;

import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.GroupByExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.SelectExpr;

import java.util.Arrays;

public final class SelectStmt implements Stmt {
    SelectExpr selectExpr, backupSelectExpr;
    TableRefsExpr tableRefsExpr;
    WhereExpr whereExpr;
    GroupByExpr groupByExpr;
    OrderByExpr orderByExpr;
    LimitExpr limitExpr;
    ForExpr forExpr;
    UnionClause unionClause;


    public SelectStmt() {
        selectExpr = new SelectExpr();
        tableRefsExpr = new TableRefsExpr();
        whereExpr = new WhereExpr();

        groupByExpr = new GroupByExpr();
        orderByExpr = new OrderByExpr();
        limitExpr = null;
        unionClause = new UnionClause();
    }

    public SelectStmt replaceSelect() {
        backupSelectExpr = selectExpr;
        selectExpr = new SelectExpr();
        return this;
    }

    public SelectStmt recoverSelect() {
        selectExpr = backupSelectExpr;
        backupSelectExpr = null;
        return this;
    }

    public SelectExpr getSelectExpr() { return selectExpr; }
    public TableRefsExpr getTableRefsExpr() { return tableRefsExpr; }
    public WhereExpr getWhereExpr() { return whereExpr; }
    public GroupByExpr getGroupByExpr() { return groupByExpr; }
    public OrderByExpr getOrderByExpr() { return orderByExpr; }
    public LimitExpr getLimitExpr() { return limitExpr; }
    public UnionClause getUnionClause() { return unionClause; }

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

    public SelectStmt table(String table) {
        return  from(table, "");
    }

    public SelectStmt table(String table, String alias) {
        return  from(table, alias);
    }

    public SelectStmt from(SelectStmt table, String alias) {
        tableRefsExpr.add(new TableRefExpr(new TableSubExpr(table, alias)));
        return  this;
    }

    public SelectStmt from(String table, String alias) {
        tableRefsExpr.add(
                new TableRefExpr(
                        new TableNameExpr(
                                new EscapedStr(table),
                                new EscapedStr(alias))));
        return this;
    }

    public SelectStmt where(WhereNest query) {
        whereExpr.where(query, false, "AND");
        return this;
    }

    public SelectStmt orWhere(WhereNest query) {
        whereExpr.where(query, false, "OR");
        return this;
    }

    public SelectStmt join(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "INNER");
    }

    public SelectStmt join(String table, WhereNest query) {
        return join(table, query, "INNER");
    }

    public SelectStmt joinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "INNER");
    }

    public SelectStmt joinSub(SelectStmt subTable, String tableAlias, WhereNest query, String joinType) {
        var whereExpr = new WhereExpr();
        whereExpr.on(query);
        var joinedExpr = new TableJoinedExpr(joinType, new TableSubExpr(subTable,  tableAlias), whereExpr);
        assert !tableRefsExpr.isEmpty();
        var refs = tableRefsExpr.getTableRefs();

        var top = refs.getLast();
        top.add(joinedExpr);

        return this;
    }

    public SelectStmt leftJoin(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "LEFT");
    }

    public SelectStmt leftJoinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "LEFT");
    }

    public SelectStmt rightJoin(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "RIGHT");
    }

    public SelectStmt rightJoinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "RIGHT");
    }

    public SelectStmt join(String table, String leftColumn, String operator, String rightColumn, String joinType) {
        var tableExpr = new TableNameExpr(new EscapedStr(table));
        var whereExpr = new WhereExpr();
        whereExpr.on(leftColumn, operator, rightColumn);
        var joinedExpr = new TableJoinedExpr(joinType, tableExpr, whereExpr);
        assert !tableRefsExpr.isEmpty();
        var refs = tableRefsExpr.getTableRefs();

        var top = refs.getLast();
        top.add(joinedExpr);

        return this;
    }

    public SelectStmt join(String table, WhereNest query, String joinType) {
        var tableExpr = new TableNameExpr(new EscapedStr(table));
        var whereExpr = new WhereExpr();
        whereExpr.on(query);
        var joinedExpr = new TableJoinedExpr(joinType, tableExpr, whereExpr);
        assert !tableRefsExpr.isEmpty();
        var refs = tableRefsExpr.getTableRefs();

        var top = refs.getLast();
        top.add(joinedExpr);

        return this;
    }

    // group by
    public SelectStmt groupBy(String... columns) {
        this.groupByExpr = new GroupByExpr();
        for (String column : columns) {
            this.groupByExpr.groupBy(new EscapedStr(column));
        }

        return this;
    }

    public SelectStmt having(WhereNest query) {
        if (groupByExpr != null) {
            groupByExpr.having(query);
        }

        return this;
    }

    // order by
    public SelectStmt orderBy(String column, String order) {
        if (orderByExpr == null) {
            orderByExpr = new OrderByExpr();
        }
        orderByExpr.add(new OrderByItem(new EscapedStr(column), order));

        return this;
    }

    // limit
    public SelectStmt limit(int rowCount) {
        limitExpr = new LimitExpr(rowCount);

        return this;
    }

    public SelectStmt limit(int rowCount, int offset) {
        limitExpr = new LimitExpr(rowCount, offset);
        return this;
    }

    // for update | share
    public SelectStmt forUpdate() {
        forExpr = new ForExpr("UPDATE");
        return this;
    }

    public SelectStmt union(SelectStmt other) {
        unionClause.add(new UnionItem("UNION", other));
        return this;
    }

}
