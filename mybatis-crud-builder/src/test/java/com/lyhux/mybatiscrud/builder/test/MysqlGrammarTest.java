package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.*;
import com.lyhux.mybatiscrud.builder.grammar.select.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignListExpr;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.BeforeEach;

public class MysqlGrammarTest {
    protected MysqlGrammar grammar;


    @BeforeEach
    public void setUp() throws Exception {
        grammar = new MysqlGrammar();
    }

    public void print(ColumnExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void print(WhereClauseExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(ExprResult result) {
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(TableNameExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void print(TableSubExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(TableFactorExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(TableJoinedExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(TableRefExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(TableRefsExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(SelectStmt stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(GroupByExpr stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(OrderByExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void print(LimitExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void print(ForExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    // insert statements
    // public void print(ColumnExpr1 expr) {
    //     var result = grammar.compile(expr);
    //     System.out.println(result);
    // }

    public void print(ValueGroupExpr stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(DuplicateAssignListExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void print(InsertStmt stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void print(UpdateStmt stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public ExprResult print(DeleteStmt stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
        return result;
    }
}
