package com.lyhux.sqlbuilder;

import com.lyhux.sqlbuilder.grammar.UpdateStmt;
import com.lyhux.sqlbuilder.grammar.WhereNest;
import com.lyhux.sqlbuilder.grammar.update.AssignNest;
import com.lyhux.sqlbuilder.vendor.Grammar;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateAdapter extends BaseAdapter {

    UpdateStmt updateStmt;

    public UpdateAdapter(Connection conn, Grammar grammar) {
        super(conn, grammar);
        this.updateStmt = new UpdateStmt();
    }

    public UpdateAdapter table(String tableName) {
        updateStmt.table(tableName);
        return this;
    }
    public UpdateAdapter where(WhereNest wrapper) {
        updateStmt.where(wrapper);
        return this;
    }
    public UpdateAdapter set(AssignNest wrapper) {
        updateStmt.set(wrapper);
        return this;
    }
    public UpdateAdapter orderBy(String column, String order) {
        updateStmt.orderBy(column, order);
        return this;
    }

    public UpdateAdapter limit(int count) {
        updateStmt.limit(count);
        return this;
    }

    public UpdateAdapter limit(int count, int offset) {
        updateStmt.limit(count, offset);
        return this;
    }

    public Long update() throws SQLException {
        return execute(grammar.compile(updateStmt), false);
    }

}
