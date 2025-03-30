package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Stack;

public class Database {
    Connection conn;
    Grammar grammar;
    Stack<Savepoint> stack;
    int transactLevel;

    public Database(Connection conn, Grammar grammar) {
        this.conn = conn;
        this.grammar = grammar;
        stack = new Stack<>();
        transactLevel = 0;
    }

    public QueryAdapter adapter(){
        return new QueryAdapter(conn, grammar);
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
