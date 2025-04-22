package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class G {
    public static void assertEquals(Grammar grammar, Stmt stmt, String statement, List<TypeValue<?>> bindings)
    {
        var result = grammar.compile(stmt);
        System.out.println(result.toSql());
        System.out.println(result.statement());
        System.out.println(result.bindings());

        Assertions.assertEquals(statement, result.statement());
        Assertions.assertEquals(bindings, result.bindings());

    }

    public static void assertEquals(Grammar grammar, Stmt stmt, String statement)
    {
        var result = grammar.compile(stmt);
        System.out.println(result.statement());
        System.out.println(result.bindings());

        Assertions.assertEquals(statement, result.statement());
    }

    public static void assertEquals(Grammar grammar, Expr expr, String statement, List<TypeValue<?>> bindings)
    {
        var result = grammar.compile(expr);
        System.out.println(result.toSql());
        System.out.println(result.statement());
        System.out.println(result.bindings());

        Assertions.assertEquals(statement, result.statement());
        Assertions.assertEquals(bindings, result.bindings());
    }

    public static void assertEquals(Grammar grammar, Expr expr, String statement)
    {
        var result = grammar.compile(expr);
        System.out.println(result.statement());
        System.out.println(result.bindings());

        Assertions.assertEquals(statement, result.statement());
    }



}
