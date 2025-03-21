package com.lyhux.mybatiscrud.builder.grammar;

import com.lyhux.mybatiscrud.builder.grammar.insert.ValueGroupExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueNest;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValuesExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.GroupByExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignNest;

import java.util.Arrays;

public class QueryBuilder {
    ColumnExpr selectExpr;
    TableRefsExpr tableRefsExpr;
    WhereExpr whereExpr;
    GroupByExpr groupByExpr;
    OrderByExpr orderByExpr;
    LimitExpr limitExpr;
    ForExpr forExpr;
    UnionClause unionClause;

    //
    ValuesExpr values;
    AssignListExpr assignments;

    public QueryBuilder() {
        selectExpr = new ColumnExpr();
        tableRefsExpr = new TableRefsExpr();
        whereExpr = new WhereExpr();

        groupByExpr = new GroupByExpr();
        orderByExpr = new OrderByExpr();
        limitExpr = null;
        unionClause = new UnionClause();

        //
        values = new ValuesExpr();
        assignments = new AssignListExpr();
    }

    public ColumnExpr getSelectExpr() { return selectExpr; }
    public TableRefsExpr getTableRefsExpr() { return tableRefsExpr; }
    public WhereExpr getWhereExpr() { return whereExpr; }
    public GroupByExpr getGroupByExpr() { return groupByExpr; }
    public OrderByExpr getOrderByExpr() { return orderByExpr; }
    public LimitExpr getLimitExpr() { return limitExpr; }
    public UnionClause getUnionClause() { return unionClause; }
    //
    public ValuesExpr getValues() { return values; }
    public AssignListExpr getAssignments() { return assignments; }

    public QueryBuilder select(String... fields) {
        selectExpr.addAll(Arrays.stream(fields).map(EscapedStr::new).toList());
        return this;
    }

    public QueryBuilder selectRaw(String field) {
        selectExpr.add(new RawStr(field));
        return this;
    }

    public QueryBuilder from(String table) {
       return  from(table, "");
    }

    public QueryBuilder table(String table) {
        return  from(table, "");
    }

    public QueryBuilder table(String table, String alias) {
        return  from(table, alias);
    }

    public QueryBuilder from(SelectStmt table, String alias) {
        tableRefsExpr.add(new TableRefExpr(new TableSubExpr(table, alias)));
        return  this;
    }

    public QueryBuilder from(String table, String alias) {
        tableRefsExpr.add(
                new TableRefExpr(
                        new TableNameExpr(
                                new EscapedStr(table),
                                new EscapedStr(alias))));
        return this;
    }

    public QueryBuilder where(WhereNest query) {
        whereExpr.where(query, false, "AND");
        return this;
    }

    public QueryBuilder orWhere(WhereNest query) {
        whereExpr.where(query, false, "OR");
        return this;
    }

    public QueryBuilder join(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "INNER");
    }

    public QueryBuilder join(String table, WhereNest query) {
        return join(table, query, "INNER");
    }

    public QueryBuilder joinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "INNER");
    }

    public QueryBuilder joinSub(SelectStmt subTable, String tableAlias, WhereNest query, String joinType) {
        var whereExpr = new WhereExpr();
        whereExpr.on(query);
        var joinedExpr = new TableJoinedExpr(joinType, new TableSubExpr(subTable,  tableAlias), whereExpr);
        assert !tableRefsExpr.isEmpty();
        var refs = tableRefsExpr.getTableRefs();

        var top = refs.getLast();
        top.add(joinedExpr);

        return this;
    }

    public QueryBuilder leftJoin(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "LEFT");
    }

    public QueryBuilder leftJoinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "LEFT");
    }

    public QueryBuilder rightJoin(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "RIGHT");
    }

    public QueryBuilder rightJoinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "RIGHT");
    }

    public QueryBuilder join(String table, String leftColumn, String operator, String rightColumn, String joinType) {
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

    public QueryBuilder join(String table, WhereNest query, String joinType) {
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
    public QueryBuilder groupBy(String... columns) {
        this.groupByExpr = new GroupByExpr();
        for (String column : columns) {
            this.groupByExpr.groupBy(new EscapedStr(column));
        }

        return this;
    }

    public QueryBuilder having(WhereNest query) {
        if (groupByExpr != null) {
            groupByExpr.having(query);
        }

        return this;
    }

    // order by
    public QueryBuilder orderBy(String column, String order) {
        if (orderByExpr == null) {
            orderByExpr = new OrderByExpr();
        }
        orderByExpr.add(new OrderByItem(new EscapedStr(column), order));

        return this;
    }

    // limit
    public QueryBuilder limit(int rowCount) {
        limitExpr = new LimitExpr(rowCount);

        return this;
    }

    public QueryBuilder limit(int rowCount, int offset) {
        limitExpr = new LimitExpr(rowCount, offset);
        return this;
    }

    // for update | share
    public QueryBuilder forUpdate() {
        forExpr = new ForExpr("UPDATE");
        return this;
    }

    public QueryBuilder union(SelectStmt other) {
        unionClause.add(new UnionItem("UNION", other));
        return this;
    }

    public QueryBuilder values(ValueNest nest) {
        var group = new ValueGroupExpr();
        values.add(group);
        nest.addValues(group);
        return this;
    }

    public QueryBuilder set(AssignNest nest) {
        nest.updateSet(assignments);
        return this;
    }
}
