package com.lyhux.mybatiscrud.builder.grammar;

import com.lyhux.mybatiscrud.builder.grammar.insert.*;

import java.util.Arrays;

public final class InsertStmt implements Stmt {
    ExprStr tableName;
    ColumnExpr columns;
    ValuesExpr values;
    AssignListExpr assigns;

    public InsertStmt() {
        this(new RawStr(""));
    }

    public InsertStmt(String tableName) {
        this(new EscapedStr(tableName));
    }

    public InsertStmt(ExprStr tableName) {
        this.tableName = tableName;
        columns = new ColumnExpr();
        values = new ValuesExpr();
        assigns = new AssignListExpr();
    }

    public ExprStr getTableName() { return tableName; }
    public ColumnExpr getColumns() { return columns; }
    public ValuesExpr getValues() { return values; }
    public AssignListExpr getAssigns() { return assigns; }

    public InsertStmt table(String tableName) {
        this.tableName = new EscapedStr(tableName);
        return this;
    }

    public InsertStmt columns(String... columns) {
        this.columns.addAll(Arrays.stream(columns).map(EscapedStr::new).toList());
        return this;
    }

    public InsertStmt values(ValueNest nest) {
        var group = new ValueGroupExpr();
        values.add(group);
        nest.addValues(group);
        return this;
    }

    public void onUpdate(AssignNest assigns) {
        assigns.assign(this.assigns);
    }
}
