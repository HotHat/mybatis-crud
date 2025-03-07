package com.lyhux.mybatiscrud.builder.grammar.insert;

import com.lyhux.mybatiscrud.builder.grammar.BindingValue;
import com.lyhux.mybatiscrud.builder.grammar.EscapedStr;
import com.lyhux.mybatiscrud.builder.grammar.RawStr;
import com.lyhux.mybatiscrud.builder.grammar.TypeValue;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValueGroupExpr {
    List<BindingValue<?>> valueExpr;

    public ValueGroupExpr() {
        valueExpr = new ArrayList<>();
    }

    public ValueGroupExpr(List<BindingValue<?>> valueExpr) { this.valueExpr = valueExpr; }

    public ValueGroupExpr addRaw(String value) {
        this.valueExpr.add(new BindingValue<>(new EscapedStr(value)));
        return this;
    }

    public <T> ValueGroupExpr add(BindingValue<T> valueExpr) {
        this.valueExpr.add(valueExpr);
        return this;
    }

    public List<BindingValue<?>> getValueExpr() { return valueExpr; }

    /** jdbc types of value */
    public ValueGroupExpr add(String value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(Integer value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(Long value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(Float value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(Double value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(Date value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(Time value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(Timestamp value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(LocalDateTime value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr add(BigDecimal value) {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of(value)));
        return this;
    }

    public ValueGroupExpr addNull() {
        this.valueExpr.add(new BindingValue<>(new RawStr("?"), TypeValue.of()));
        return this;
    }


}
