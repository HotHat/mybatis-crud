package com.lyhux.mybatiscrud.builder.grammar;

public sealed interface Stmt
    permits DeleteStmt, InsertStmt, SelectStmt, UpdateStmt {
}
