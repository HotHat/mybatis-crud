package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.bean.BeanMapUtil;
import com.lyhux.mybatiscrud.builder.grammar.ExprResult;
import com.lyhux.mybatiscrud.builder.grammar.SelectStmt;
import com.lyhux.mybatiscrud.builder.grammar.WhereNest;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.*;
import java.util.*;

public class SelectAdapter extends BaseAdapter {
    SelectStmt selectStmt;

    static boolean isLogQuery = false;
    static List<ExprResult> queryLogs;
    Class<?> beanQualifier;

    public SelectAdapter(Connection conn, Grammar grammar) {
        super(conn, grammar);
        selectStmt = new SelectStmt();
    }

    public SelectAdapter qualifier(Class<?> beanQualifier) {
        this.beanQualifier = beanQualifier;
        return this;
    }

    public SelectAdapter select(String... fields) {
        selectStmt.select(fields);
        return this;
    }

    public SelectAdapter selectRaw(String field) {
        selectStmt.selectRaw(field);
        return this;
    }

    public SelectAdapter from(String table) {
        selectStmt.from(table);
        return this;
    }

    public SelectAdapter from(String table, String alias) {
        selectStmt.from(table, alias);
        return this;
    }

    public SelectAdapter table(String table) {
        selectStmt.from(table);
        return this;
    }

    public SelectAdapter table(String table, String alias) {
        selectStmt.from(table, alias);
        return this;
    }

    public SelectAdapter where(WhereNest wrapper) {
        selectStmt.where(wrapper);
        return this;
    }

    public SelectAdapter orWhere(WhereNest wrapper) {
        selectStmt.orWhere(wrapper);
        return this;
    }


    public SelectAdapter join(String table, String leftColumn, String operator, String rightColumn) {
        selectStmt.join(table, leftColumn, operator, rightColumn);
        return this;
    }

    public SelectAdapter join(String table, WhereNest wrapper) {
        selectStmt.join(table, wrapper);
        return this;
    }

    public SelectAdapter joinSub(SelectStmt subTable, String alias, WhereNest query) {
        selectStmt.joinSub(subTable, alias, query);
        return this;
    }

    public SelectAdapter leftJoin(String table, String leftColumn, String operator, String rightColumn) {
        selectStmt.leftJoin(table, leftColumn, operator, rightColumn);
        return this;
    }

    public SelectAdapter leftJoinSub(SelectStmt subTable, String alias, WhereNest query) {
        selectStmt.joinSub(subTable, alias, query);
        return this;
    }

    public SelectAdapter rightJoin(String table, String leftColumn, String operator, String rightColumn) {
        selectStmt.rightJoin(table, leftColumn, operator, rightColumn);
        return this;
    }

    public SelectAdapter rightJoinSub(SelectStmt subTable, String alias, WhereNest query) {
        selectStmt.rightJoinSub(subTable, alias, query);
        return this;
    }

    public SelectAdapter groupBy(String... columns) {
        selectStmt.groupBy(columns);
        return this;
    }

    public SelectAdapter having(WhereNest query) {
        selectStmt.having(query);
        return this;
    }

    public SelectAdapter orderBy(String column, String order) {
        selectStmt.orderBy(column, order);
        return this;
    }

    public SelectAdapter limit(int rowCount) {
        selectStmt.limit(rowCount);
        return this;
    }

    public SelectAdapter limit(int rowCount, int offset) {
        selectStmt.limit(rowCount, offset);
        return this;
    }

    public Optional<Map<String, Object>> first() throws Exception {
        selectStmt.limit(1);

        var result = get();
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.getFirst());
        }
    }
    public<T> Optional<T> first(Class<T> bean) throws Exception {
        var result = first();
        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(BeanMapUtil.mapToBean(result.get(), bean));
    }

    public List<?> getQualifier() throws Exception {

        var all = get();
        var result = new ArrayList<>(all.size());
        for (Map<String, Object> stringObjectMap : all) {
            var item = BeanMapUtil.mapToBean(stringObjectMap, beanQualifier);
            result.add(item);
        }

        return result;
    }

    public <T> List<T> get(Class<T> bean) throws Exception {
        var all = get();
        var result = new ArrayList<T>(all.size());
        for (Map<String, Object> stringObjectMap : all) {
            var item = BeanMapUtil.mapToBean(stringObjectMap, bean);
            result.add(item);
        }

        return result;
    }

    public List<Map<String, Object>> get() throws Exception {
        var result = new ArrayList<Map<String, Object>>();

        var compileResult = grammar.compile(selectStmt);

        if (isLogQuery) {
            queryLogs.add(compileResult);
        }

        PreparedStatement prepare =  conn.prepareStatement(compileResult.statement());
        int count = 1;
        for (var binding : compileResult.bindings()) {
            prepare.setObject(count++, binding.value());
        }

        ResultSet rs = prepare.executeQuery();

        while (rs.next()) {
            Map<String, Object> rowData = new HashMap<>();

            var meta = rs.getMetaData();
            int numberOfColumns = meta.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                rowData.put(meta.getColumnName(i), rs.getObject(i));
            }

            // var target = BeanMapUtil.mapToBean(rowData, clazz);
            result.add(rowData);
        }

        return result;
    }

}
