package com.lyhux.mybatiscrud.builder.grammar.insert;

import java.util.ArrayList;
import java.util.List;

public class ValuesExpr {
    List<ValueGroupExpr> values;

    public ValuesExpr() {
        values = new ArrayList<ValueGroupExpr>();
    }

    public void add(ValueGroupExpr value) {
        values.add(value);
    }

    public List<ValueGroupExpr> getValues() { return values; }
}
