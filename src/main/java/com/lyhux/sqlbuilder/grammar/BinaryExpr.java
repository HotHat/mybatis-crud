package com.lyhux.sqlbuilder.grammar;

public final class BinaryExpr implements WhereClauseExpr {
   private final ExprStr column;
   private final String operator;
   private final BindingValue<?> value;
   private final boolean showBrace;
  // private final ExprStr value;
  // private final List<ExprValue<?>> bindings;

    public BinaryExpr(ExprStr column, String operator, BindingValue<?> value) {
        this(column,operator, value, false);
    }

    public BinaryExpr(ExprStr column, String operator, BindingValue<?> value, boolean braceValue) {
        this.column = column;
        this.operator = operator;
        this.value = value;
        // this.bindings = bindings;
        this.showBrace = braceValue;
    }

    public boolean isShowBrace() { return showBrace; }

    public ExprStr getColumn() {
        return column;
    }

    public String getOperator() {
        return operator;
    }

    public BindingValue<?> getValue() {
        return value;
    }

    // public List<ExprValue<?>> getBindings() {
    //     return bindings;
    // }

}
