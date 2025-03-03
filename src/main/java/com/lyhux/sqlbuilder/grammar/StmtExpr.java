package com.lyhux.sqlbuilder.grammar;

import java.util.List;

public record StmtExpr<T>(ExprStr expr, SelectStmt stmt, List<ExprValue<T>> binding) {
    public StmtExpr(ExprStr expr, List<ExprValue<T>> binding) {
        this(expr, null, binding);
    }

    public StmtExpr(SelectStmt stmt) {
        this(null, stmt, null);
    }

    public boolean isExpr() { return this.expr != null; }
    public boolean isStmt() { return this.stmt != null; }

}
