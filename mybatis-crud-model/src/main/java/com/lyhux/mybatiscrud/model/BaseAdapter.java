package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.builder.grammar.ExprResult;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BaseAdapter {

    protected Connection conn;
    protected Grammar grammar;

    public BaseAdapter(Connection conn, Grammar grammar) {
        this.conn = conn;
        this.grammar = grammar;
    }

    public Long execute(ExprResult result, boolean isInsert) throws SQLException {
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
