package com.lyhux.mybatiscrud.builder.vendor;

import com.lyhux.mybatiscrud.builder.grammar.ExprResult;
import com.lyhux.mybatiscrud.builder.grammar.Stmt;

public interface Grammar {
    ExprResult compile(Stmt stmt);
}
