package com.lyhux.mybatiscrud.builder.grammar;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class WhereExpr implements WhereClauseExpr {
    String bool = "AND";
    // boolean root = false;
    boolean showBraces = false;

    List<WhereClauseExpr> conditions = new ArrayList<>();

    public WhereExpr() { showBraces = false; }
    public WhereExpr(String bool) { this.bool = bool; }
    public WhereExpr(boolean showBraces) {  this.showBraces = showBraces; }
    public WhereExpr(String bool, boolean showBrackets) { this.bool = bool; this.showBraces = showBrackets; }

    public String getBool() { return bool; }
    // public boolean isRoot() { return root; }
    public boolean isShowBraces() { return showBraces; }

    public List<WhereClauseExpr> getConditions() { return conditions; }
    public boolean isEmpty() { return conditions.isEmpty(); }

    public void add(WhereClauseExpr expr) {
        conditions.add(expr);
        // if (!showBraces) {
        //     showBraces = conditions.size() > 1 && !isRoot();
        // }
    }

    public void add(WhereClauseExpr expr, String bool) {
        conditions.add(expr);
        this.bool = bool;
        // if (!showBraces) {
        //     showBraces = conditions.size() > 1 && !isRoot();
        // }
    }


    // Where column value group
    public WhereExpr where(String column, String value) {
        return where(column, "=", TypeValue.of(JDBCType.VARCHAR, value));
    }

    public WhereExpr where(String column, Integer value) {
        return where(column, "=", TypeValue.of(JDBCType.INTEGER, value));
    }

    public WhereExpr where(String column, Long value) {
        return where(column, "=", TypeValue.of(JDBCType.BIGINT, value));
    }

    public WhereExpr where(String column, Float value) {
        return where(column, "=", TypeValue.of(JDBCType.FLOAT, value));
    }

    public WhereExpr where(String column, Double value) {
        return where(column, "=", TypeValue.of(JDBCType.DOUBLE, value));
    }

    public WhereExpr where(String column, Date value) {
        return where(column, "=", TypeValue.of(JDBCType.DATE, value));
    }

    public WhereExpr where(String column, Time value) {
        return where(column, "=", TypeValue.of(JDBCType.TIME, value));
    }

    public WhereExpr where(String column, Timestamp value) {
        return where(column, "=", TypeValue.of(JDBCType.TIMESTAMP, value));
    }

    public WhereExpr where(String column, LocalDateTime value) {
        return where(column, "=", TypeValue.of(JDBCType.TIMESTAMP, value));
    }

    public WhereExpr where(String column, BigDecimal value) {
        return where(column, "=", TypeValue.of(JDBCType.DECIMAL, value));
    }

    // orWhere column = value group
    public WhereExpr orWhere(String column, String value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.VARCHAR, value));
    }

    public WhereExpr orWhere(String column, Integer value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.INTEGER, value));
    }

    public WhereExpr orWhere(String column, Long value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.BIGINT, value));
    }

    public WhereExpr orWhere(String column, Float value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.FLOAT, value));
    }

    public WhereExpr orWhere(String column, Double value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.DOUBLE, value));
    }

    public WhereExpr orWhere(String column, Date value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.DATE, value));
    }

    public WhereExpr orWhere(String column, Time value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.TIME, value));
    }

    public WhereExpr orWhere(String column, Timestamp value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.TIMESTAMP, value));
    }

    public WhereExpr orWhere(String column, LocalDateTime value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.TIMESTAMP, value));
    }

    public WhereExpr orWhere(String column, BigDecimal value) {
        return orWhere(column, "=", TypeValue.of(JDBCType.DECIMAL, value));
    }


    // where column operator value group
    public WhereExpr where(String column, String operator, String value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, Integer value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, Long value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, Float value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, Double value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, Date value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, Time value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, Timestamp value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, LocalDateTime value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr where(String column, String operator, BigDecimal value) {
        return where(column, operator, TypeValue.of(value));
    }

    public WhereExpr whereColumn(String column, String value) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
                new EscapedStr(column),
                "=",
                new BindingValue<>(new EscapedStr(value))
        ), "AND");

        add(expr, "AND");
        return this;
    }

    public WhereExpr whereNull(String column) {
        return whereNull(column, true, true);
    }

    public WhereExpr whereNotNull(String column) {
        return whereNull(column, false, true);
    }

    public WhereExpr orWhereNull(String column) {
        return whereNull(column, true, false);
    }

    public WhereExpr orWhereNotNull(String column) {
        return whereNull(column, false, false);
    }

    private WhereExpr whereNull(String column, boolean isNull, boolean isAnd) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
            new EscapedStr(column),
            isNull ? "IS NULL" : "IS NOT NULL",
            new BindingValue<>(new RawStr(""), List.of())
        ), isAnd ? "AND" : "OR");

        add(expr, isAnd ? "AND" : "OR");
        return this;
    }

    public WhereExpr whereIn(String column, List<String> value) {
        return baseWhere(column, "IN", value, JDBCType.VARCHAR, true, false);
    }

    public WhereExpr whereIn(String column, String... value) {
        return baseWhere(column, "IN", value, JDBCType.VARCHAR, true, false);
    }

    public WhereExpr whereIn(String column, Integer... value) {
        return baseWhere(column, "IN", value, JDBCType.INTEGER, true, false);
    }

    public WhereExpr whereIn(String column, Long... value) {
        return baseWhere(column, "IN", value, JDBCType.BIGINT, true, false);
    }

    public WhereExpr whereIn(String column, Float... value) {
        return baseWhere(column, "IN", value, JDBCType.FLOAT, true, false);
    }

    public WhereExpr whereIn(String column, Double... value) {
        return baseWhere(column, "IN", value, JDBCType.DOUBLE, true, false);
    }

    public WhereExpr whereIn(String column, Date... value) {
        return baseWhere(column, "IN", value, JDBCType.DATE, true, false);
    }

    public WhereExpr whereIn(String column, Time... value) {
        return baseWhere(column, "IN", value, JDBCType.TIME, true, false);
    }

    public WhereExpr whereIn(String column, Timestamp... value) {
        return baseWhere(column, "IN", value, JDBCType.TIMESTAMP, true, false);
    }

    public WhereExpr whereIn(String column, LocalDateTime... values) {
        return whereIn(true, column, values);
    }

    public WhereExpr whereIn(boolean isAnd, String column, LocalDateTime... values) {
        var expr = new WhereExpr(false);
        var bindings = new ArrayList<TypeValue<Timestamp>>();
        int count = 0;
        var mark = new StringBuilder();
        mark.append("(");
        for (var t : values) {
            bindings.add(TypeValue.of(t));
            mark.append("?");
            if (++count < values.length) {
                mark.append(", ");
            }
        }
        mark.append(")");
        expr.add(new BinaryExpr(
            new EscapedStr(column),
            "IN",
            new BindingValue<>(new RawStr(mark.toString()), bindings)
        ), isAnd ? "AND" : "OR");

        add(expr, isAnd ? "AND" : "OR");

        return this;
    }

    public WhereExpr whereIn(String column, BigDecimal... value) {
        return baseWhere(column, "IN", value, JDBCType.DECIMAL, false, false);
    }

    // orWhereIn group
    public WhereExpr orWhereIn(String column, String... value) {
        return baseWhere(column, "IN", value, JDBCType.VARCHAR, false, false);
    }

    public WhereExpr orWhereIn(String column, Integer... value) {
        return baseWhere(column, "IN", value, JDBCType.INTEGER, false, false);
    }

    public WhereExpr orWhereIn(String column, Long... value) {
        return baseWhere(column, "IN", value, JDBCType.BIGINT, false, false);
    }

    public WhereExpr orWhereIn(String column, Float... value) {
        return baseWhere(column, "IN", value, JDBCType.FLOAT, false, false);
    }

    public WhereExpr orWhereIn(String column, Double... value) {
        return baseWhere(column, "IN", value, JDBCType.DOUBLE, false, false);
    }

    public WhereExpr orWhereIn(String column, Date... value) {
        return baseWhere(column, "IN", value, JDBCType.DATE, false, false);
    }

    public WhereExpr orWhereIn(String column, Time... value) {
        return baseWhere(column, "IN", value, JDBCType.TIME, false, false);
    }

    public WhereExpr orWhereIn(String column, Timestamp... value) {
        return baseWhere(column, "IN", value, JDBCType.TIMESTAMP, false, false);
    }

    public WhereExpr orWhereIn(String column, LocalDateTime... values) {
        return whereIn(false, column, values);
    }

    public WhereExpr orWhereIn(String column, BigDecimal... value) {
        return baseWhere(column, "IN", value, JDBCType.DECIMAL, false, false);
    }

    public WhereExpr on(String column, String operator, String value) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
                new EscapedStr(column),
                operator,
                new BindingValue<>(new EscapedStr(value), new ArrayList<TypeValue<String>>())
        ), "AND");

        add(expr, "ON");
        return this;
    }

    public WhereExpr on(WhereNest query) {
        where(query, true, "ON");
        return this;
    }


    public WhereExpr where(WhereNest query) {
        return  where(query, true, "AND");
    }

    public WhereExpr where(WhereNest query, boolean showBraces, String bool) {
        var expr = new WhereExpr(showBraces);
        add(expr, bool);
        query.where(expr);
        return this;
    }

    public WhereExpr orWhere(WhereNest query) {
        var expr = new WhereExpr("OR", true);
        add(expr);
        query.where(expr);
        return this;
    }

    public <T> WhereExpr where(String column, String operator, TypeValue<T> value) {
        return baseWhere(column, operator, value, true, false);
    }

    public <T> WhereExpr orWhere(String column, String operator, TypeValue<T> value) {
        return baseWhere(column, operator, value, false, false);
    }

    public WhereExpr whereRaw(String column, List<TypeValue<String>> values) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
            new RawStr(column),
            "",
            new BindingValue<>(new RawStr(""), values)
        ), "AND");

        add(expr);

        return this;
    }

    // orWhere group
    public WhereExpr orWhere(String column, String operator, String value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, Integer value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, Long value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, Float value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, Double value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, Date value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, Time value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, Timestamp value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, LocalDateTime value) {
        return baseWhere(column, operator, TypeValue.of(value), false, false);
    }

    public WhereExpr orWhere(String column, String operator, BigDecimal value) {
        return baseWhere(column, operator, TypeValue.of(JDBCType.DECIMAL, value), false, false);
    }

    // orWhere just one type with raw string value
    public WhereExpr orWhereRaw(String column, String operator, String value) {
        return baseWhere(column, operator, TypeValue.of(JDBCType.VARCHAR, value), false, true);
    }

    public <T> WhereExpr baseWhere(String column, String operator, TypeValue<T> value, boolean isAnd, boolean isRaw) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
                isRaw ? new RawStr(column) : new EscapedStr(column),
                operator,
                new BindingValue<>(new RawStr("?"), List.of(value))
        ), isAnd ? "AND" : "OR");

        add(expr);

        return this;
    }

    public <T> WhereExpr baseWhere(String column, String operator, T[] params, JDBCType type, boolean isAnd, boolean isRaw) {
        return baseWhere(column, operator, List.of(params), type, isAnd, isRaw);
    }

    public <T> WhereExpr baseWhere(String column, String operator, List<T> params, JDBCType type, boolean isAnd, boolean isRaw) {
        var expr = new WhereExpr();
        var values = new ArrayList<TypeValue<T>>();
        int count = 0;
        var mark = new StringBuilder();
        mark.append("(");
        for (T t : params) {
            values.add(new TypeValue<>(type, t));
            mark.append("?");
            if (++count < params.size()) {
                mark.append(", ");
            }
        }
        mark.append(")");
        expr.add(new BinaryExpr(
            isRaw ? new RawStr(column) : new EscapedStr(column),
            operator,
            new BindingValue<>(new RawStr(mark.toString()), values)
        ), isAnd ? "AND" : "OR");

        add(expr);

        return this;
    }

    //
    public WhereExpr whereExists(SelectStmt stmt) {
        var expr = new WhereExpr(true);

        expr.add(new BinaryExpr(
                new RawStr(""),
                "exists",
                new BindingValue<>(stmt),
                true
        ), "AND");

        add(expr);

        return this;
    }

    public WhereExpr when(boolean test, WhereNest query) {
        if (test) {
            where(query, true, "AND");
        }
        return this;
    }

    public WhereExpr orWhen(boolean test, WhereNest query) {
        if (test) {
            where(query, true, "OR");
        }
        return this;
    }
}
