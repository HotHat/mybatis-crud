package com.lyhux.mybatiscrud.builder.grammar.insert;

import com.lyhux.mybatiscrud.builder.grammar.EscapedStr;
import com.lyhux.mybatiscrud.builder.grammar.Expr;

import java.util.ArrayList;
import java.util.List;

public class AssignListExpr implements Expr {
    List<AssignExpr> assignExpr;

    public AssignListExpr() {
        assignExpr = new ArrayList<>();
    }


    public boolean isEmpty() { return assignExpr.isEmpty(); }

    public <T>AssignListExpr add(AssignExpr assignExpr) {
        this.assignExpr.add(assignExpr);
        return this;
    }

    public AssignListExpr add(String column, String value) {
        add(new AssignExpr(new EscapedStr(column), new EscapedStr(value)));
        return this;
    }

    public List<AssignExpr> getAssignExpr() { return assignExpr; }
}
