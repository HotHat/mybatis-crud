package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueNest;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignNest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Builder extends Query {
    private final QueryAdapter adapter;

    public Builder(QueryAdapter adapter) {
        this.adapter = adapter;
    }

    public Builder select(String... fields) { super.select(fields); return this; }

    public Builder select(String field, String... bindings) { super.selectRaw(field, bindings); return this; }

    public Builder from(String from) { super.from(from); return this; }
    public Builder table(String table) { super.table(table); return this; }
    public Builder table(String table, String alias) { super.table(table, alias); return this; }
    public Builder from(Query table, String alias) { super.from(table, alias); return this; }
    public Builder from(String table, String alias) { super.from(table, alias); return this; }

    public Builder where(WhereNest query) { super.where(query); return this; }

    public Builder join(String table, String leftColumn, String operator, String rightColumn) {
        super.join(table, leftColumn, operator, rightColumn);
        return this;
    }

    public Builder join(String table, WhereNest query) { super.join(table, query); return this; }

    public Builder leftJoin(String table, String leftColumn, String operator, String rightColumn) {
        super.leftJoin(table, leftColumn, operator, rightColumn);
        return this;
    }
    public Builder rightJoin(String table, String leftColumn, String operator, String rightColumn) {
        super.rightJoin(table, leftColumn, operator, rightColumn);
        return this;
    }

    public Builder joinSub(Query subTable, String alias, WhereNest query) {
        super.joinSub(subTable, alias, query);
        return this;
    }
    public Builder leftJoinSub(Query subTable, String alias, WhereNest query) {
        super.leftJoinSub(subTable, alias, query);
        return this;
    }
    public Builder rightJoinSub(Query subTable, String alias, WhereNest query) {
        super.rightJoinSub(subTable, alias, query);
        return this;
    }

    public Builder groupBy(String... columns) { super.groupBy(columns); return this; }
    public Builder having(WhereNest query) { super.having(query); return this; }
    public Builder orderBy(String column, String order) { super.orderBy(column, order); return this; }
    public Builder limit(int limit) { super.limit(limit); return this; }
    public Builder limit(int rowCount, int offset) { super.limit(rowCount, offset); return this; }
    public Builder when(boolean cond, QueryNest query) { super.when(cond, query); return this; }
    public Builder forUpdate() { super.forUpdate(); return this; }
    public Builder union(Query other) { super.union(other); return this; }
    public Builder columns(String... fields) { super.columns(fields); return this; }
    public Builder values(ValueNest nest) { super.values(nest); return this; }
    public Builder set(AssignNest nest) { super.set(nest); return this; }

    public ExprResult dump() {
        adapter.query(this);
        return adapter.dump();
    }


    public List<Map<String, Object>> get() throws Exception {
        adapter.query(this);
        return adapter.get();
    }

    public<T> List<T> get(Class<T> bean) throws Exception {
        adapter.query(this);
        return adapter.get(bean);
    }

    public Optional<Map<String, Object>> first() throws Exception
    {
        adapter.query(this);
        return adapter.first();
    }

    public<T> Optional<T> first(Class<T> bean) throws Exception
    {
        adapter.query(this);
        return adapter.first(bean);
    }

    public <T> Page<T> paginate(int page, int pageSize, Class<T> bean) throws Exception {
        adapter.query(this);
        return adapter.paginate(page, pageSize, bean);
    }


    public Page<Map<String, Object>> paginate(int page, int pageSize) throws SQLException {
        adapter.query(this);
        return adapter.paginate(page, pageSize);

    }
}
