package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueNest;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignNest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Builder {
    private final QueryAdapter adapter;
    private final Query query;
    public Builder(QueryAdapter adapter) {
        this.adapter = adapter;
        this.query = new Query();
    }

    public Builder select(String... fields) { query.select(fields); return this; }

    public Builder select(String field, String... bindings) { query.selectRaw(field, bindings); return this; }

    public Builder from(String from) { query.from(from); return this; }
    public Builder table(String table) { query.table(table); return this; }
    public Builder table(String table, String alias) { query.table(table, alias); return this; }
    public Builder from(Query table, String alias) { query.from(table, alias); return this; }
    public Builder from(String table, String alias) { query.from(table, alias); return this; }

    public Builder where(WhereNest query) { this.query.where(query); return this; }

    public Builder join(String table, String leftColumn, String operator, String rightColumn) {
        query.join(table, leftColumn, operator, rightColumn);
        return this;
    }

    public Builder join(String table, WhereNest query) { this.query.join(table, query); return this; }

    public Builder leftJoin(String table, String leftColumn, String operator, String rightColumn) {
        query.leftJoin(table, leftColumn, operator, rightColumn);
        return this;
    }
    public Builder rightJoin(String table, String leftColumn, String operator, String rightColumn) {
        query.rightJoin(table, leftColumn, operator, rightColumn);
        return this;
    }

    public Builder joinSub(Query subTable, String alias, WhereNest query) {
        this.query.joinSub(subTable, alias, query);
        return this;
    }
    public Builder leftJoinSub(Query subTable, String alias, WhereNest query) {
        this.query.leftJoinSub(subTable, alias, query);
        return this;
    }
    public Builder rightJoinSub(Query subTable, String alias, WhereNest query) {
        this.query.rightJoinSub(subTable, alias, query);
        return this;
    }

    public Builder groupBy(String... columns) { query.groupBy(columns); return this; }
    public Builder having(WhereNest query) { this.query.having(query); return this; }
    public Builder orderBy(String column, String order) { query.orderBy(column, order); return this; }
    public Builder limit(int limit) { query.limit(limit); return this; }
    public Builder limit(int rowCount, int offset) { query.limit(rowCount, offset); return this; }
    public Builder when(boolean cond, QueryNest query) { this.query.when(cond, query); return this; }
    public Builder forUpdate() { query.forUpdate(); return this; }
    public Builder union(Query other) { query.union(other); return this; }
    public Builder columns(String... fields) { query.columns(fields); return this; }
    public Builder values(ValueNest nest) { query.values(nest); return this; }
    public Builder set(AssignNest nest) { query.set(nest); return this; }

    public ExprResult dump() {
        adapter.query(this.query);
        return adapter.dump();
    }


    public List<Map<String, Object>> get() throws Exception {
        adapter.query(this.query);
        return adapter.get();
    }

    public<T> List<T> get(Class<T> bean) throws Exception {
        adapter.query(this.query);
        return adapter.get(bean);
    }

    public Optional<Map<String, Object>> first() throws Exception
    {
        adapter.query(this.query);
        return adapter.first();
    }

    public<T> Optional<T> first(Class<T> bean) throws Exception
    {
        adapter.query(this.query);
        return adapter.first(bean);
    }

    public <T> Page<T> paginate(int page, int pageSize, Class<T> bean) throws Exception {
        adapter.query(this.query);
        return adapter.paginate(page, pageSize, bean);
    }


    public Page<Map<String, Object>> paginate(int page, int pageSize) throws SQLException {
        adapter.query(this.query);
        return adapter.paginate(page, pageSize);

    }
}
