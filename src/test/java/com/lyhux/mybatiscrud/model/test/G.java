package com.lyhux.mybatiscrud.model.test;

import com.lyhux.mybatiscrud.builder.grammar.Expr;
import com.lyhux.mybatiscrud.builder.grammar.ExprResult;
import com.lyhux.mybatiscrud.builder.grammar.Stmt;
import com.lyhux.mybatiscrud.builder.grammar.TypeValue;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class G {
    public static void assertEquals(ExprResult result, String statement)
    {
        System.out.println(result.statement());
        System.out.println(result.bindings());

        Assertions.assertEquals(statement, result.statement());
    }

    public static void assertEquals(ExprResult result, String statement, List<TypeValue<?>> bindings)
    {
        System.out.println(result.toSql());
        System.out.println(result.statement());
        System.out.println(result.bindings());

        Assertions.assertEquals(statement, result.statement());
        Assertions.assertEquals(bindings, result.bindings());
    }

}
