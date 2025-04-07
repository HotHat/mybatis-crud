package com.lyhux.mybatiscrud.builder.grammar.insert;

import com.lyhux.mybatiscrud.builder.grammar.BindingValue;
import com.lyhux.mybatiscrud.builder.grammar.Expr;
import com.lyhux.mybatiscrud.builder.grammar.ExprStr;

public record DuplicateAssignExpr(ExprStr column, BindingValue<?> value) implements Expr {

}
