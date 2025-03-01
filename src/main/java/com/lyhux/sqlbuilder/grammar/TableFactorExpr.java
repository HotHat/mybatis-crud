package main.java.com.lyhux.sqlbuilder.grammar;

public sealed interface TableFactorExpr extends Expr
permits TableNameExpr, TableSubExpr{
}
