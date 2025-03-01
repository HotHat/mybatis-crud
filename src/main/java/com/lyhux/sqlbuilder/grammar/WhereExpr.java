package main.java.com.lyhux.sqlbuilder.grammar;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

public final class WhereExpr implements WhereClauseExpr {
    String bool = "AND";
    boolean root = false;
    WhereExpr parent = null;

    List<WhereClauseExpr> conditions = new ArrayList<>();

    public WhereExpr() { root = true; }
    public WhereExpr(boolean root) { this.root = root; }
    public WhereExpr(WhereExpr parent) { this.root = false; this.parent = parent; }

    public String getBool() { return bool; }
    public boolean isRoot() { return root; }
    public List<WhereClauseExpr> getConditions() { return conditions; }

    public void add(WhereClauseExpr expr, String bool) {
        conditions.add(expr);
        this.bool = bool;
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

    public WhereExpr whereIn(String column, List<String> value) {
        return baseWhere(column, "IN", value, JDBCType.INTEGER, true, true);
    }


    public WhereExpr on(String column, String operator, String value) {
        var expr = new WhereExpr();
        expr.add(new BinaryExpr(
                new RawStr(column),
                operator,
                new RawStr(value),
                new ArrayList<ExprValue<?>>()
        ), "ON");

        conditions.add(expr);
        return this;
    }


    public WhereExpr where(WhereNest query) {
        var expr = new WhereExpr(false);
        add(expr, "AND");
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
                new RawStr("?"),
                List.of(new ExprValue<>(type, value))
        ), isAnd ? "AND" : "OR");

        conditions.add(expr);

        return this;
    }

    public <T> WhereExpr baseWhere(String column, String operator, List<T> params, JDBCType type, boolean isAnd, boolean isRaw) {
        var expr = new WhereExpr(false);
        var values = new ArrayList<ExprValue<?>>();
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
                new RawStr(mark.toString()),
                values
        ), isAnd ? "AND" : "OR");

        conditions.add(expr);

        return this;
    }


}
