package com.lyhux.mybatiscrud.builder.grammar.insert;

import com.lyhux.mybatiscrud.builder.grammar.*;

import java.util.ArrayList;
import java.util.List;

public class DuplicateAssignListExpr implements Expr {
    List<DuplicateAssignExpr> assignExpr;

    public DuplicateAssignListExpr() {
        assignExpr = new ArrayList<>();
    }


    public boolean isEmpty() { return assignExpr.isEmpty(); }

    public <T> DuplicateAssignListExpr add(DuplicateAssignExpr assignExpr) {
        this.assignExpr.add(assignExpr);
        return this;
    }

    public DuplicateAssignListExpr addColumn(String column, String value) {
        add(new DuplicateAssignExpr(new EscapedStr(column), new BindingValue<>(new EscapedStr(value))));
        return this;
    }

    public DuplicateAssignListExpr addRaw(String column, String value) {
        add(new DuplicateAssignExpr(new EscapedStr(column), new BindingValue<>(new RawStr(value))));
        return this;
    }

    public DuplicateAssignListExpr add(String column, String value) {
        add(new DuplicateAssignExpr(new EscapedStr(column), new BindingValue<>(new RawStr("?"), TypeValue.of(value))));
        return this;
    }


    public List<DuplicateAssignExpr> getAssignExpr() { return assignExpr; }
}
