package com.lyhux.mybatiscrud.builder.grammar.insert;

import com.lyhux.mybatiscrud.builder.grammar.EscapedStr;
import com.lyhux.mybatiscrud.builder.grammar.Expr;

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

    public DuplicateAssignListExpr add(String column, String value) {
        add(new DuplicateAssignExpr(new EscapedStr(column), new EscapedStr(value)));
        return this;
    }

    public List<DuplicateAssignExpr> getAssignExpr() { return assignExpr; }
}
