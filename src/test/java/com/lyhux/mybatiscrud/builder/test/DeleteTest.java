package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.ExprResult;
import com.lyhux.mybatiscrud.builder.grammar.Query;
import com.lyhux.mybatiscrud.builder.grammar.TypeValue;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DeleteTest {
    static final Grammar mysqlGrammar = new MysqlGrammar();

    @Test
    public void test() {
        // var delete = new DeleteStmt("user");
        var delete = new Query()
            .table("user")
            .where((query)-> {
                query.where("id", 5);
            })
            .orderBy("id", "desc")
            .limit(10)
            .toDeleteStmt()
        ;

        G.assertEquals(
            mysqlGrammar,
            delete,
            "DELETE FROM `user` WHERE `id` = ? ORDER BY `id` DESC LIMIT 10",
            List.of(
                TypeValue.of(5)
            )
        );
    }

}
