package com.lyhux.sqlbuilder;

import com.lyhux.sqlbuilder.vendor.MysqlGrammar;
import org.junit.jupiter.api.Test;

public class BuilderTest {
    @Test
    public void testBuilder() {
        var builder = new Builder(null, new MysqlGrammar());

        builder
            .select()
            .from("user")
            .where((query) -> {
                query.where("id", 1);
            })
        ;

        var result = builder.compile();
        System.out.println(result);
    }

}
