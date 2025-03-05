package com.lyhux.sqlbuilder;

import com.lyhux.sqlbuilder.grammar.SelectStmt;
import com.lyhux.sqlbuilder.grammar.WhereNest;
import com.lyhux.sqlbuilder.vendor.Grammar;

import java.sql.*;
import java.util.*;

public class SelectAdapter extends BaseAdapter {
    SelectStmt selectStmt;

    public SelectAdapter(Connection conn, Grammar grammar) {
        super(conn, grammar);
        selectStmt = new SelectStmt();
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

    public<T> Optional<T> first(Class<T> clazz) throws Exception {
        selectStmt.limit(1);

        var result = get(clazz);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    public <T> List<T> get(Class<T> clazz) throws Exception {
        var result = new ArrayList<T>();

        var compileResult = grammar.compile(selectStmt);

        PreparedStatement stm =  conn.prepareStatement(compileResult.statement());
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            Map<String, Object> rowData = new HashMap<>();

            var meta = rs.getMetaData();
            int numberOfColumns = meta.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                rowData.put(meta.getColumnName(i), rs.getObject(i));
            }

            var target = BeanMapUtil.mapToBean(rowData, clazz);
            result.add(target);
        }

        return result;
    }


}
