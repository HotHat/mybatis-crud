package com.lyhux.mybatiscrud.builder.grammar.update;

import com.lyhux.mybatiscrud.builder.grammar.BindingValue;
import com.lyhux.mybatiscrud.builder.grammar.Expr;
import com.lyhux.mybatiscrud.builder.grammar.ExprStr;

public record AssignExpr<T>(ExprStr column, BindingValue<T> value) implements Expr {

}
