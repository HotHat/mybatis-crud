package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.bean.BeanMapUtil;
import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class QueryAdapter {

    protected Query builder;
    protected Connection conn;
    protected Grammar grammar;

    static boolean isLogQuery = false;
    static List<ExprResult> queryLogs;
    Class<?> beanQualifier;

    public QueryAdapter(Connection conn, Grammar grammar) {
        this.conn = conn;
        this.grammar = grammar;
        builder = new Query();

        queryLogs = new ArrayList<>();
    }

    public QueryAdapter query(QueryNest queryNest) {
        queryNest.query(builder);
        return this;
    }

    public QueryAdapter query(Query builder) {
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

    public <T> Page<T> paginate(Class<T> bean) throws Exception {
        var pageMap = paginate();

        var result = new ArrayList<T>(pageMap.records().size());
        for (Map<String, Object> map: pageMap.records()) {
            var item = BeanMapUtil.mapToBean(map, bean);
            result.add(item);
        }

        return new Page<>(pageMap.page(), pageMap.pageSize(), pageMap.total(), result);
    }

    public Page<Map<String, Object>> paginate() throws SQLException {
        if (builder.getPaginate() == null) {
            return new Page<>(0, 0, 0, new ArrayList<>());
        }

        var paginate = builder.getPaginate();
        int page = paginate.page();
        int pageSize = paginate.pageSize();

        Long total = 0L;
        if (!builder.getGroupByExpr().isEmpty() || !builder.getUnionClause().isEmpty()) {
            total = getGroupByOrUnionAggregate();
        } else {
            total = getSimpleAggregate();
        }

        page = page <= 0 ? 1 : page;
        int offset = (page - 1) * pageSize;

        builder.limit(pageSize, offset);

        ExprResult paginateCompile = grammar.compile(builder.toSelectStmt());
        var records = getQueryResult(paginateCompile);

        return new Page<>(page, pageSize, Math.toIntExact(total), records);
    }

    private Long getSimpleAggregate() throws SQLException {
        var aggregate = new ColumnExpr().add(new RawStr("count(*) AS aggregate"));
        var aggregateStmt = new SelectStmt(
            aggregate,
            builder.getTableRefsExpr(),
            builder.getWhereExpr(),
            builder.getGroupByExpr(),
            new OrderByExpr(),
            null,
            null,
            new UnionClause()
        );

        ExprResult aggregateResult = grammar.compile(aggregateStmt);

        var totalMap = getQueryResult(aggregateResult);
        if (totalMap.isEmpty()) {
            return 0L;
        }

        return (Long)totalMap.getFirst().getOrDefault("aggregate", 0);
    }

    private Long getGroupByOrUnionAggregate() throws SQLException {
        var aggregate = new ColumnExpr().add(new RawStr("count(*) AS aggregate"));

        var totalStmt = new SelectStmt(
            new ColumnExpr().add(new RawStr("*")),
            builder.getTableRefsExpr(),
            builder.getWhereExpr(),
            builder.getGroupByExpr(),
            new OrderByExpr(),
            null,
            null,
            builder.getUnionClause()
        );

        var aggregateStmt = new SelectStmt(
            aggregate,
            new TableRefsExpr().add(new TableRefExpr(new TableSubExpr(totalStmt, "temp_table"))),
            builder.getWhereExpr(),
            builder.getGroupByExpr(),
            new OrderByExpr(),
            null,
            null,
            new UnionClause()
        );

        ExprResult aggregateResult = grammar.compile(aggregateStmt);

        var totalMap = getQueryResult(aggregateResult);
        if (totalMap.isEmpty()) {
            return 0L;
        }

        return (Long)totalMap.getFirst().getOrDefault("aggregate", 0);
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

        var compileResult = grammar.compile(builder.toSelectStmt());
        return getQueryResult(compileResult);
    }

    private List<Map<String, Object>> getQueryResult(ExprResult compileResult) throws SQLException {
        var result = new ArrayList<Map<String, Object>>();

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
