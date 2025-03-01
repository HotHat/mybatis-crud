package main.java.com.lyhux.sqlbuilder.grammar;

import main.java.com.lyhux.sqlbuilder.StmtValue;

import java.util.List;

public final class BinaryExpr implements WhereClauseExpr {
   private final ExprStr column;
   private final String operator;
   private final ExprStr value;
   private final List<ExprValue<?>> bindings;

   public BinaryExpr(ExprStr column, String operator, ExprStr value, List<ExprValue<?>> bindings) {
       this.column = column;
       this.operator = operator;
       this.value = value;
       this.bindings = bindings;
   }

    public ExprStr getColumn() {
        return column;
    }

    public String getOperator() {
        return operator;
    }

    public ExprStr getValue() {
        return value;
    }

    public List<ExprValue<?>> getBindings() {
        return bindings;
    }

}
