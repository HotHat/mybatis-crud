package com.lyhux.mybatiscrud.builder.grammar;

public class TableJoinedExpr implements Expr {
    String joined;
    TableFactorExpr tableFactor;
    WhereExpr condition;

    public TableJoinedExpr(TableFactorExpr tableFactor, WhereExpr condition) {
        this.joined = "INNER";
        this.tableFactor = tableFactor;
        this.condition = condition;
    }

    public TableJoinedExpr(String joined, TableFactorExpr tableFactor, WhereExpr condition) {
        this.joined = joined;
        this.tableFactor = tableFactor;
        this.condition = condition;
    }

    public String getJoined() { return joined; }
    public TableFactorExpr getTableFactor() { return tableFactor; }
    public WhereExpr getCondition() { return condition; }
}
