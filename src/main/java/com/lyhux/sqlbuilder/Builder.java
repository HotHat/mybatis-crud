package com.lyhux.sqlbuilder;

import com.lyhux.sqlbuilder.grammar.*;
import com.lyhux.sqlbuilder.vendor.Grammar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Builder {
    Connection conn;
    Grammar grammar;
    Stmt stmt;

    public Builder(Connection conn, Grammar grammar) {
        this.conn = conn;
        this.grammar = grammar;
    }

    public Builder select(SelectStmtNest select) {
        var selectStmt = new SelectStmt();
        select.builder(selectStmt);
        stmt = selectStmt;
        return this;
    }

    public SelectStmt select() {
        var selectStmt = new SelectStmt();
        stmt = selectStmt;
        return selectStmt;
    }

    public Long insert(InsertStmtNest insert) throws SQLException {
        var insertStmt = new InsertStmt();
        insert.builder(insertStmt);
        // stmt = insertStmt;
        var exprValue = grammar.compile(insertStmt);

        return dmlExecute(exprValue, true);
    }

    public Long update(UpdateStmtNest update) throws SQLException {
        var updateStmt = new UpdateStmt();
        update.builder(updateStmt);
        // stmt = insertStmt;
        var exprValue = grammar.compile(updateStmt);

        return dmlExecute(exprValue, false);
    }

    public ExprResult compile() {
        return grammar.compile(stmt);
    }



    public Long dmlExecute(ExprResult result, boolean isInsert) throws SQLException {
        var prepare = conn.prepareStatement(
            result.statement(),
            isInsert ? PreparedStatement.RETURN_GENERATED_KEYS : PreparedStatement.NO_GENERATED_KEYS
        );
        // prepare.execute();

        int count = 1;
        for (var it : result.bindings()) {
            prepare.setObject(count++, it.value());
        }

        int ret = prepare.executeUpdate();
        // insert return primary key
        if (ret == 1 && isInsert) {
            var primaryKeySet = prepare.getGeneratedKeys();
            if (primaryKeySet.next()) {
                return (long)primaryKeySet.getInt(1);
            }
        }

        return (long) ret;
    }

}
