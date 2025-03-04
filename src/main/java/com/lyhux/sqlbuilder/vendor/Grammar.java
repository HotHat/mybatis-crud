package com.lyhux.sqlbuilder.vendor;

import com.lyhux.sqlbuilder.grammar.ExprResult;
import com.lyhux.sqlbuilder.grammar.Stmt;

public interface Grammar {
    ExprResult compile(Stmt stmt);
}
