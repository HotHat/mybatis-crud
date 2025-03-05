package com.lyhux.sqlbuilder;

import com.lyhux.sqlbuilder.grammar.DeleteStmt;
import com.lyhux.sqlbuilder.grammar.WhereNest;
import com.lyhux.sqlbuilder.vendor.Grammar;

import java.sql.Connection;
import java.sql.SQLException;

public class DeleteAdapter extends BaseAdapter {

    DeleteStmt deleteStmt;

    public DeleteAdapter(Connection conn, Grammar grammar) {
        super(conn, grammar);
        this.deleteStmt = new DeleteStmt();
    }

    public DeleteAdapter table(String tableName) {
        deleteStmt.table(tableName);
        return this;
    }
    public DeleteAdapter where(WhereNest wrapper) {
        deleteStmt.where(wrapper);
        return this;
    }

    public DeleteAdapter orderBy(String column, String order) {
        deleteStmt.orderBy(column, order);
        return this;
    }

    public DeleteAdapter limit(int count) {
        deleteStmt.limit(count);
        return this;
    }

    public DeleteAdapter limit(int count, int offset) {
        deleteStmt.limit(count, offset);
        return this;
    }

    public Long delete() throws SQLException {
        return execute(grammar.compile(deleteStmt), false);
    }

}
