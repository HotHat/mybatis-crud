package com.lyhux.sqlbuilder.grammar.insert;

import com.lyhux.sqlbuilder.grammar.EscapedStr;
import com.lyhux.sqlbuilder.grammar.Expr;
import com.lyhux.sqlbuilder.grammar.ExprStr;

public record AssignExpr(ExprStr column, ExprStr value) implements Expr {

}
