package com.lyhux.mybatiscrud.builder;

import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Stack;

public class Builder {
    Connection conn;
    Grammar grammar;
    Stack<Savepoint> stack;
    int transactLevel;

    public Builder(Connection conn, Grammar grammar) {
        this.conn = conn;
        this.grammar = grammar;
        stack = new Stack<>();
        transactLevel = 0;
    }

    public SelectAdapter selectQuery(){
        return new SelectAdapter(conn, grammar);
    }

    public InsertAdapter insertQuery(){
        return new InsertAdapter(conn, grammar);
    }

    public DeleteAdapter deleteQuery(){
        return new DeleteAdapter(conn, grammar);
    }

    public UpdateAdapter updateQuery(){
        return new UpdateAdapter(conn, grammar);
    }

    public void beginTransaction() throws SQLException {
        transactLevel++;

        if (transactLevel == 1) {
            conn.setAutoCommit(false);
        } else {
            var savepoint = conn.setSavepoint();
            stack.push(savepoint);
        }
    }

    public void rollback() throws SQLException {
        if (transactLevel > 1) {
            transactLevel--;
            var savepoint = stack.pop();
            conn.rollback(savepoint);
        } else if (transactLevel == 1) {
            conn.rollback();
        } else {
            transactLevel = 0;
        }
    }

    public void commit() throws SQLException {
        if (transactLevel > 1) {
            transactLevel--;
            var savepoint = stack.pop();
            conn.releaseSavepoint(savepoint);
        } else if (transactLevel == 1) {
            conn.commit();
            conn.setAutoCommit(true);
        } else {
            transactLevel = 0;
        }
    }

}
