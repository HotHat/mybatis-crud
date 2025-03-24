package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.DeleteStmt;
import com.lyhux.mybatiscrud.builder.grammar.QueryBuilder;
import org.junit.jupiter.api.Test;

public class DeleteTest extends MysqlGrammarTest {
    @Test

    public void test() {
        // var delete = new DeleteStmt("user");
        var delete = new QueryBuilder()
            .table("user")
            .where((query)-> {
                query.where("id", 5);
            })
            .orderBy("id", "desc")
            .limit(10)
            .toDeleteStmt()
        ;

        print(delete);
    }

}
