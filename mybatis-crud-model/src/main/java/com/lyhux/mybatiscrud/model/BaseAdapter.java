package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.builder.grammar.ExprResult;
import com.lyhux.mybatiscrud.builder.grammar.Stmt;
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

    public Long execute(ExprResult result, boolean genKey) throws SQLException {
        var prepare = conn.prepareStatement(
            result.statement(),
            genKey ? PreparedStatement.RETURN_GENERATED_KEYS : PreparedStatement.NO_GENERATED_KEYS
        );
        // prepare.execute();

        int count = 1;
        for (var binding : result.bindings()) {
            prepare.setObject(count++, binding.value());
        }

        int ret = prepare.executeUpdate();
        // insert return primary key
        if (ret == 1 && genKey) {
            var primaryKeySet = prepare.getGeneratedKeys();
            if (primaryKeySet.next()) {
                return (long)primaryKeySet.getInt(1);
            }
        }

        return (long) ret;
    }

    public ExprResult toSql(Stmt stmt){
        return grammar.compile(stmt);
    }
}
