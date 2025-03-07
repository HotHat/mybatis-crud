package com.lyhux.mybatiscrud.builder;

import com.lyhux.mybatiscrud.builder.grammar.InsertStmt;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueNest;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.Connection;
import java.sql.SQLException;

public class InsertAdapter extends BaseAdapter {
    InsertStmt insertStmt;

    public InsertAdapter(Connection conn, Grammar grammar) {
        super(conn, grammar);
        insertStmt = new InsertStmt();
    }

    public InsertAdapter table(String tableName) {
        insertStmt.table(tableName);
        return this;
    }

    public InsertAdapter values(ValueNest nest) {
        insertStmt.values(nest);
        return this;
    }

    public InsertAdapter columns(String... columns) {
        insertStmt.columns(columns);
        return this;
    }

    public Long insert() throws SQLException {
        return execute(grammar.compile(insertStmt), true);
    }

}
