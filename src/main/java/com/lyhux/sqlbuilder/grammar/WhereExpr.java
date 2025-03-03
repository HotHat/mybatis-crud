package com.lyhux.sqlbuilder.grammar;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

public final class WhereExpr implements WhereClauseExpr {
    String bool = "AND";
    boolean root = false;
    boolean showBraces = false;

    List<WhereClauseExpr> conditions = new ArrayList<>();

    public WhereExpr() { root = true; }
    public WhereExpr(boolean root) { this.root = root; showBraces = false; }
    public WhereExpr(boolean root, boolean showBrackets) { this.root = root; this.showBraces = showBrackets; }

    public String getBool() { return bool; }
    public boolean isRoot() { return root; }
    public boolean isShowBraces() { return showBraces; }

    public List<WhereClauseExpr> getConditions() { return conditions; }
    public boolean isEmpty() { return conditions.isEmpty(); }

    public void add(WhereClauseExpr expr, String bool) {
        conditions.add(expr);
        this.bool = bool;

        showBraces = conditions.size() > 1 && !isRoot();
    }

    public WhereExpr where(String column, String value) {
        return where(column, "=", value, JDBCType.VARCHAR);
    }

    public WhereExpr where(String column, Integer value) {
        return where(column, "=", value, JDBCType.INTEGER);
    }

    public WhereExpr where(String column, String operator, Integer value) {
        return where(column, operator, value, JDBCType.INTEGER);
    }

    public WhereExpr where(String column, String operator, String value) {
        return where(column, operator, value, JDBCType.VARCHAR);
    }

    public WhereExpr whereColumn(String column, String value) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
                new EscapedStr(column),
                "=",
                new StmtExpr<>(new EscapedStr(value), new ArrayList<ExprValue<String>>())
        ), "AND");

        conditions.add(expr);
        return this;
    }

    public WhereExpr whereIn(String column, List<String> value) {
        return baseWhere(column, "IN", value, JDBCType.INTEGER, true, false);
    }


    public WhereExpr on(String column, String operator, String value) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
                new EscapedStr(column),
                operator,
                new StmtExpr<>(new EscapedStr(value), new ArrayList<ExprValue<String>>())
        ), "ON");

        conditions.add(expr);
        return this;
    }


    public WhereExpr where(WhereNest query) {
        return  where(query, false, "AND");
    }

    public WhereExpr where(WhereNest query, boolean isRoot, String bool) {
        var expr = new WhereExpr(isRoot);
        add(expr, bool);
        query.where(expr);
        return this;
    }


    public WhereExpr orWhere(WhereNest query) {
        var expr = new WhereExpr();
        add(expr, "OR");
        query.where(expr);
        return this;
    }

    public <T> WhereExpr where(String column, String operator, T value, JDBCType type) {
        return baseWhere(column, operator, value, type, true, false);
    }

    public <T> WhereExpr whereRaw(String column, String operator, T value, JDBCType type) {
        return baseWhere(column, operator, value, type, true, true);
    }

    public <T> WhereExpr orWhere(String column, String operator, T value, JDBCType type) {
        return baseWhere(column, operator, value, type, false, false);
    }

    public <T> WhereExpr orWhereRaw(String column, String operator, T value, JDBCType type) {
        return baseWhere(column, operator, value, type, false, true);
    }

    public <T> WhereExpr baseWhere(String column, String operator, T value, JDBCType type, boolean isAnd, boolean isRaw) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
                isRaw ? new RawStr(column) : new EscapedStr(column),
                operator,
                new StmtExpr<>(new RawStr("?"), List.of(new ExprValue<>(type, value)))
        ), isAnd ? "AND" : "OR");

        conditions.add(expr);

        return this;
    }

    public <T> WhereExpr baseWhere(String column, String operator, List<T> params, JDBCType type, boolean isAnd, boolean isRaw) {
        var expr = new WhereExpr(false);
        var values = new ArrayList<ExprValue<T>>();
        int count = 0;
        var mark = new StringBuilder();
        mark.append("(");
        for (T t : params) {
            values.add(new ExprValue<>(type, t));
            mark.append("?");
            if (++count < params.size()) {
                mark.append(", ");
            }
        }
        mark.append(")");
       expr.add(new BinaryExpr(
                isRaw ? new RawStr(column) : new EscapedStr(column),
                operator,
                new StmtExpr<>(new RawStr(mark.toString()), values)
        ), isAnd ? "AND" : "OR");

        conditions.add(expr);

        return this;
    }

    //
    public WhereExpr whereExists(SelectStmt stmt) {
        var expr = new WhereExpr(false, true);

        expr.add(new BinaryExpr(
                new RawStr(""),
                "exists",
                new StmtExpr<>(stmt),
                true
        ), "AND");

        conditions.add(expr);

        return this;
    }

}
