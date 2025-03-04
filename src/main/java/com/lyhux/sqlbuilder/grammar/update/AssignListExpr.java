package com.lyhux.sqlbuilder.grammar.update;

import com.lyhux.sqlbuilder.grammar.*;

import java.util.ArrayList;
import java.util.List;

public class AssignListExpr implements Expr {
    List<AssignExpr<?>> assignExpr;

    public AssignListExpr() {
        assignExpr = new ArrayList<>();
    }


    public boolean isEmpty() { return assignExpr.isEmpty(); }

    public <T>AssignListExpr add(AssignExpr<T> assignExpr) {
        this.assignExpr.add(assignExpr);
        return this;
    }

    public AssignListExpr addRaw(String column, String value) {
        add(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new EscapedStr(value))));
        return this;
    }

    public AssignListExpr add(String column, String value) {
        add(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public AssignListExpr add(String column, Integer value) {
        add(new AssignExpr<>(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }

    public List<AssignExpr<?>> getAssignExpr() { return assignExpr; }
}
