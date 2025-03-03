package com.lyhux.sqlbuilder.grammar.insert;

import com.lyhux.sqlbuilder.grammar.EscapedStr;
import com.lyhux.sqlbuilder.grammar.ExprValue;
import com.lyhux.sqlbuilder.grammar.RawStr;
import com.lyhux.sqlbuilder.grammar.ValueExpr;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ValueGroupExpr {
    List<ValueExpr<?>> valueExpr;

    public ValueGroupExpr() {
        valueExpr = new ArrayList<>();
    }

    public ValueGroupExpr(List<ValueExpr<?>> valueExpr) { this.valueExpr = valueExpr; }

    public ValueGroupExpr addRaw(String val) {
        this.valueExpr.add(new ValueExpr<>(new EscapedStr(val)));
        return this;
    }

    public ValueGroupExpr add(String val) {
        this.valueExpr.add(new ValueExpr<>(new RawStr("?"), ExprValue.of(val)));
        return this;
    }

    public ValueGroupExpr add(Integer val) {
        this.valueExpr.add(new ValueExpr<>(new RawStr("?"), ExprValue.of(val)));
        return this;
    }

    public ValueGroupExpr add(Date value) {
        this.valueExpr.add(new ValueExpr<>(new RawStr("?"), ExprValue.of(value)));
        return this;
    }


    public <T> ValueGroupExpr add(ValueExpr<T> valueExpr) {
        this.valueExpr.add(valueExpr);
        return this;
    }

    public List<ValueExpr<?>> getValueExpr() { return valueExpr; }
}
