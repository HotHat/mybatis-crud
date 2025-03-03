package com.lyhux.sqlbuilder.grammar.insert;

import com.lyhux.sqlbuilder.grammar.ValueExpr;

import java.util.ArrayList;
import java.util.List;

public class ValueGroupExpr {
    List<ValueExpr<?>> valueExpr;

    public ValueGroupExpr() {
        valueExpr = new ArrayList<>();
    }

    public ValueGroupExpr(List<ValueExpr<?>> valueExpr) { this.valueExpr = valueExpr; }

    public <T> ValueGroupExpr add(ValueExpr<T> valueExpr) {
        this.valueExpr.add(valueExpr);
        return this;
    }

    public List<ValueExpr<?>> getValueExpr() { return valueExpr; }
}
