package com.lyhux.mybatiscrud.builder.grammar;

public final class TableNameExpr implements TableFactorExpr {
    ExprStr tableName;
    ExprStr alias;

    public TableNameExpr(String tableName) {
        this.tableName = new EscapedStr(tableName, true);
        alias = new RawStr("");
    }

    public TableNameExpr(ExprStr tableName) {
        this.tableName = tableName;
        alias = new RawStr("");
    }

    public TableNameExpr(ExprStr tableName, ExprStr alias) {
        this.tableName = tableName;
        this.alias = alias;
    }

    public ExprStr getTableName() { return tableName; }
    public ExprStr getAlias() { return alias; }
}
