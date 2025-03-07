package com.lyhux.mybatiscrud.builder.grammar.insert;

import com.lyhux.mybatiscrud.builder.grammar.Expr;
import com.lyhux.mybatiscrud.builder.grammar.ExprStr;

public record AssignExpr(ExprStr column, ExprStr value) implements Expr {

}
