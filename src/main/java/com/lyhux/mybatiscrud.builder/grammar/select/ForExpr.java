package com.lyhux.mybatiscrud.builder.grammar.select;

import com.lyhux.mybatiscrud.builder.grammar.EscapedStr;
import com.lyhux.mybatiscrud.builder.grammar.Expr;
import com.lyhux.mybatiscrud.builder.grammar.ExprStr;

import java.util.ArrayList;
import java.util.List;

public class ForExpr implements Expr {
    String model;
    List<ExprStr> tableNames;

    public ForExpr(String model) { this.model = model; tableNames = new ArrayList<ExprStr>(); }

    public ForExpr(String model, List<ExprStr> tableNames) {
        this.model = model;
        this.tableNames = tableNames;
    }

    public void of(String...  tableNames) {
        for (String tableName : tableNames) {
            this.tableNames.add(new EscapedStr(tableName));
        }
    }

    public String getModel() { return this.model; }
    public List<ExprStr> getTableNames() { return this.tableNames; }
}
