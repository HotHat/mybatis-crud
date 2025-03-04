package com.lyhux.sqlbuilder.grammar;

import org.junit.jupiter.api.Test;

public class DeleteTest extends MysqlGrammarTest {
    @Test

    public void test() {
        var delete = new DeleteStmt("user");

        delete
            .where((query)-> {
                query.where("id", 5);
            })
            .orderBy("id", "desc")
            .limit(10)
        ;

        print(delete);
    }

}
