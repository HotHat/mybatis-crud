package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignListExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueGroupExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.GroupByExpr;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class MysqlGrammarTest {
    protected MysqlGrammar grammar;


    @BeforeEach
    public void setUp() throws Exception {
        grammar = new MysqlGrammar();
    }

    public void exprAssert(ColumnExpr expr, ExprResult expected) {
        var result = grammar.compile(expr);
        System.out.println(result);

        Assertions.assertEquals(expected, result);
    }

    public void exprAssert(WhereClauseExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(ExprResult result) {
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(TableNameExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void exprAssert(TableSubExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(TableFactorExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(TableJoinedExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(TableRefExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(TableRefsExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(SelectStmt stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(GroupByExpr stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(OrderByExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void exprAssert(LimitExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void exprAssert(ForExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    // insert statements
    // public void print(ColumnExpr1 expr) {
    //     var result = grammar.compile(expr);
    //     System.out.println(result);
    // }

    public void exprAssert(ValueGroupExpr stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(DuplicateAssignListExpr expr) {
        var result = grammar.compile(expr);
        System.out.println(result);
    }

    public void exprAssert(InsertStmt stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public void exprAssert(UpdateStmt stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
    }

    public ExprResult exprAssert(DeleteStmt stmt) {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());
        return result;
    }
}
