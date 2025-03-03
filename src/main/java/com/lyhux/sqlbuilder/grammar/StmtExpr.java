package com.lyhux.sqlbuilder.grammar;

public record StmtExpr(ExprStr expr, SelectStmt stmt) {
    public StmtExpr(ExprStr expr) {
        this(expr, null);
    }

    public StmtExpr(SelectStmt stmt) {
        this(null, stmt);
    }

    public boolean isExpr() { return this.expr != null; }
    public boolean isStmt() { return this.stmt != null; }

}
