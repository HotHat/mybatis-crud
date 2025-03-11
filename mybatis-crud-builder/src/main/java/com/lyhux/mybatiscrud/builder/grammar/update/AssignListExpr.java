package com.lyhux.mybatiscrud.builder.grammar.update;

import com.lyhux.mybatiscrud.builder.grammar.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssignListExpr implements Expr {
    List<AssignExpr<?>> assignExpr;

    public AssignListExpr() {
        assignExpr = new ArrayList<>();
    }


    public boolean isEmpty() { return assignExpr.isEmpty(); }

    public <T>AssignListExpr set(AssignExpr<T> assignExpr) {
        this.assignExpr.add(assignExpr);
        return this;
    }

    public AssignListExpr setRaw(String column, String value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new EscapedStr(value))));
        return this;
    }

    public AssignListExpr set(String column, String value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, Integer value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, Long value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, Float value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, Double value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, Date value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, Time value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, Timestamp value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, LocalDateTime value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr set(String column, BigDecimal value) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr setNull(String column) {
        set(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of())));
        return this;
    }


    public List<AssignExpr<?>> getAssignExpr() { return assignExpr; }
}
