package com.lyhux.mybatiscrud.builder.grammar;

import com.lyhux.mybatiscrud.builder.grammar.insert.*;
import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.GroupByExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignNest;

import java.util.Arrays;

public class Query {
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
    DuplicateAssignListExpr duplicateAssigns;

    //
    Paginate paginate;

    public Query() {
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
        duplicateAssigns = new DuplicateAssignListExpr();

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
    //
    public Paginate getPaginate() { return paginate; }


    public Query select(String... fields) {
        selectExpr.addAll(Arrays.stream(fields).map(EscapedStr::new).toList());
        return this;
    }

    public Query selectRaw(String field) {
        selectExpr.add(new RawStr(field));
        return this;
    }

    public Query from(String table) {
       return  from(table, "");
    }

    public Query table(String table) {
        return  from(table, "");
    }

    public Query table(String table, String alias) {
        return  from(table, alias);
    }

    public Query from(SelectStmt table, String alias) {
        tableRefsExpr.add(new TableRefExpr(new TableSubExpr(table, alias)));
        return  this;
    }

    public Query from(Query table, String alias) {
        tableRefsExpr.add(new TableRefExpr(new TableSubExpr(table.toSelectStmt(), alias)));
        return  this;
    }

    public Query from(String table, String alias) {
        tableRefsExpr.add(
                new TableRefExpr(
                        new TableNameExpr(
                                new EscapedStr(table),
                                new EscapedStr(alias))));
        return this;
    }

    public Query where(WhereNest query) {
        whereExpr.where(query, false, "AND");
        return this;
    }

    public Query orWhere(WhereNest query) {
        whereExpr.where(query, false, "OR");
        return this;
    }

    public Query join(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "INNER");
    }

    public Query join(String table, WhereNest query) {
        return join(table, query, "INNER");
    }

    public Query joinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "INNER");
    }

    public Query joinSub(SelectStmt subTable, String tableAlias, WhereNest query, String joinType) {
        var whereExpr = new WhereExpr();
        whereExpr.on(query);
        var joinedExpr = new TableJoinedExpr(joinType, new TableSubExpr(subTable,  tableAlias), whereExpr);
        assert !tableRefsExpr.isEmpty();
        var refs = tableRefsExpr.getTableRefs();

        var top = refs.getLast();
        top.add(joinedExpr);

        return this;
    }

    public Query leftJoin(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "LEFT");
    }

    public Query leftJoinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "LEFT");
    }

    public Query rightJoin(String table, String leftColumn, String operator, String rightColumn) {
        return join(table, leftColumn, operator, rightColumn, "RIGHT");
    }

    public Query rightJoinSub(SelectStmt subTable, String alias, WhereNest query) {
        return joinSub(subTable, alias, query, "RIGHT");
    }

    public Query join(String table, String leftColumn, String operator, String rightColumn, String joinType) {
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

    public Query join(String table, WhereNest query, String joinType) {
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
    public Query groupBy(String... columns) {
        this.groupByExpr = new GroupByExpr();
        for (String column : columns) {
            this.groupByExpr.groupBy(new EscapedStr(column));
        }

        return this;
    }

    public Query having(WhereNest query) {
        if (groupByExpr != null) {
            groupByExpr.having(query);
        }

        return this;
    }

    // order by
    public Query orderBy(String column, String order) {
        if (orderByExpr == null) {
            orderByExpr = new OrderByExpr();
        }
        orderByExpr.add(new OrderByItem(new EscapedStr(column), order));

        return this;
    }

    // limit
    public Query limit(int rowCount) {
        limitExpr = new LimitExpr(rowCount);

        return this;
    }

    public Query limit(int rowCount, int offset) {
        limitExpr = new LimitExpr(rowCount, offset);
        return this;
    }

    // for update | share
    public Query forUpdate() {
        forExpr = new ForExpr("UPDATE");
        return this;
    }

    public Query union(SelectStmt other) {
        unionClause.add(new UnionItem("UNION", other));
        return this;
    }

    public Query union(Query other) {
        return union(other.toSelectStmt());
    }

    public Query columns(String... fields) {
        return select(fields);
    }

    public Query values(ValueNest nest) {
        var group = new ValueGroupExpr();
        values.add(group);
        nest.addValues(group);
        return this;
    }

    public Query set(AssignNest nest) {
        nest.updateSet(assignments);
        return this;
    }
    public void onUpdate(DuplicateAssignNest assigns) {
        assigns.assign(this.duplicateAssigns);
    }

    public SelectStmt toSelectStmt() {
        return new SelectStmt(
            selectExpr,
            tableRefsExpr,
            whereExpr,
            groupByExpr,
            orderByExpr,
            limitExpr,
            forExpr,
            unionClause
        );
    }

    public Query paginate(int page, int pageSize) {
        paginate = new Paginate(page, pageSize);
        return this;
    }

    public InsertStmt toInsertStmt() {
        var refs = tableRefsExpr.getTableRefs();
        TableRefExpr top;
        if (refs.isEmpty()) {
            top = new TableRefExpr(new TableNameExpr(new EscapedStr("")));
        } else {
            top = refs.getLast();
        }

        return new InsertStmt(
            top,
            selectExpr,
            values,
            duplicateAssigns
        );
    }

    public UpdateStmt toUpdateStmt() {
        var refs = tableRefsExpr.getTableRefs();
        TableRefExpr top;
        if (refs.isEmpty()) {
            top = new TableRefExpr(new TableNameExpr(new EscapedStr("")));
        } else {
            top = refs.getLast();
        }

        return new UpdateStmt(
            top,
            assignments,
            whereExpr,
           orderByExpr,
           limitExpr
        );
    }

    public DeleteStmt toDeleteStmt() {
        var refs = tableRefsExpr.getTableRefs();
        TableRefExpr top;
        if (refs.isEmpty()) {
            top = new TableRefExpr(new TableNameExpr(new EscapedStr("")));
        } else {
            top = refs.getLast();
        }

        return new DeleteStmt(
            top,
            whereExpr,
            orderByExpr,
            limitExpr
        );
    }


}
