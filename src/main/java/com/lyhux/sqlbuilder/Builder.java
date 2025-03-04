package com.lyhux.sqlbuilder;

import com.lyhux.sqlbuilder.grammar.*;
import com.lyhux.sqlbuilder.vendor.Grammar;

import java.sql.Connection;

public class Builder {
    Connection conn;
    Grammar grammar;
    Stmt stmt;

    public Builder(Connection conn, Grammar grammar) {
        this.conn = conn;
        this.grammar = grammar;
    }

    public Builder select(SelectStmtNest select) {
        var selectStmt = new SelectStmt();
        select.builder(selectStmt);
        stmt = selectStmt;
        return this;
    }

    public SelectStmt select() {
        var selectStmt = new SelectStmt();
        stmt = selectStmt;
        return selectStmt;
    }

    public ExprResult compile() {
        return grammar.compile(stmt);
    }

}
