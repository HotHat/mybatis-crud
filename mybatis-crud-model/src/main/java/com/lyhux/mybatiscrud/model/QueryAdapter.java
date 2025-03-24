package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.bean.BeanMapUtil;
import com.lyhux.mybatiscrud.builder.grammar.ExprResult;
import com.lyhux.mybatiscrud.builder.grammar.QueryBuilder;
import com.lyhux.mybatiscrud.builder.grammar.QueryNest;
import com.lyhux.mybatiscrud.builder.grammar.Stmt;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class QueryAdapter {

    protected QueryBuilder builder;
    protected Connection conn;
    protected Grammar grammar;

    static boolean isLogQuery = false;
    static List<ExprResult> queryLogs;
    Class<?> beanQualifier;

    public QueryAdapter(Connection conn, Grammar grammar) {
        this.conn = conn;
        this.grammar = grammar;
        builder = new QueryBuilder();

        queryLogs = new ArrayList<>();
    }

    public QueryAdapter query(QueryNest queryNest) {
        queryNest.query(builder);
        return this;
    }

    public QueryAdapter query(QueryBuilder builder) {
        this.builder = builder;
        return this;
    }

    public void insert() throws SQLException {
        execute(grammar.compile(builder.toInsertStmt()), false);
    }

    public Long insertGetId() throws SQLException {
        return execute(grammar.compile(builder.toInsertStmt()), true);
    }

    public Optional<Map<String, Object>> first() throws Exception {
        builder.limit(1);

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

    public QueryAdapter qualifier(Class<?> beanQualifier) {
        this.beanQualifier = beanQualifier;
        return this;
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

        var compileResult = grammar.compile(builder.toSelectStmt());

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

    public Long update() throws SQLException {
        return execute(grammar.compile(builder.toUpdateStmt()), false);
    }

    public Long delete() throws SQLException {
        return execute(grammar.compile(builder.toDeleteStmt()), false);
    }

    public Long execute(ExprResult result, boolean genKey) throws SQLException {
        var prepare = conn.prepareStatement(
            result.statement(),
            genKey ? PreparedStatement.RETURN_GENERATED_KEYS : PreparedStatement.NO_GENERATED_KEYS
        );
        // prepare.execute();

        int count = 1;
        for (var binding : result.bindings()) {
            prepare.setObject(count++, binding.value());
        }

        int ret = prepare.executeUpdate();
        // insert return primary key
        if (ret == 1 && genKey) {
            var primaryKeySet = prepare.getGeneratedKeys();
            if (primaryKeySet.next()) {
                return (long)primaryKeySet.getInt(1);
            }
        }

        return (long) ret;
    }

    public ExprResult toSql(Stmt stmt){
        return grammar.compile(stmt);
    }
}
