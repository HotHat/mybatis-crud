package com.lyhux.sqlbuilder.grammar;

public sealed interface Stmt
        permits DeleteStmt, InsertStmt, SelectStmt, UpdateStmt {
}
