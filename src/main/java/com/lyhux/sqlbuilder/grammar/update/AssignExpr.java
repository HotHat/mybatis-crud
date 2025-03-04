package com.lyhux.sqlbuilder.grammar.update;

import com.lyhux.sqlbuilder.grammar.BindingValue;
import com.lyhux.sqlbuilder.grammar.Expr;
import com.lyhux.sqlbuilder.grammar.ExprStr;

public record AssignExpr<T>(ExprStr column, BindingValue<T> value) implements Expr {

}
