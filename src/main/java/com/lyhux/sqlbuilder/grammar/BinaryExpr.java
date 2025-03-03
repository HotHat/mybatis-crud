package com.lyhux.sqlbuilder.grammar;

import java.util.List;

public final class BinaryExpr implements WhereClauseExpr {
   private final ExprStr column;
   private final String operator;
   private final StmtExpr<?> value;
   private boolean braceValue;
  // private final ExprStr value;
  // private final List<ExprValue<?>> bindings;

    public BinaryExpr(ExprStr column, String operator, StmtExpr<?> value) {
        this(column,operator, value, false);
    }

    public BinaryExpr(ExprStr column, String operator, StmtExpr<?> value, boolean braceValue) {
        this.column = column;
        this.operator = operator;
        this.value = value;
        // this.bindings = bindings;
        this.braceValue = braceValue;
    }

    public boolean isBraceValue() { return braceValue; }

    public ExprStr getColumn() {
        return column;
    }

    public String getOperator() {
        return operator;
    }

    public StmtExpr<?> getValue() {
        return value;
    }

    // public List<ExprValue<?>> getBindings() {
    //     return bindings;
    // }

}
