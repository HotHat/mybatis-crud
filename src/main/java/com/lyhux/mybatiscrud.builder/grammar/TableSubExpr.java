package com.lyhux.mybatiscrud.builder.grammar;

public  record TableSubExpr(SelectStmt selectStmt, String alias) implements TableFactorExpr {

}
