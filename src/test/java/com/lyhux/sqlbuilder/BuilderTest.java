package com.lyhux.sqlbuilder;

import com.lyhux.sqlbuilder.vendor.MysqlGrammar;
import org.junit.jupiter.api.Test;

public class BuilderTest {
    @Test
    public void testBuilder() {
        var builder = new Builder(null, new MysqlGrammar());
        var test1 = true;
        var test2 = false;

        var selector = builder
            .select()
            .from("user")
            .select("id", "name", "age")
            .where((query) -> {
                query.where("id", 1)
                     .whereIn("name", "haha", "baba")
                     .when(test1, (q) -> {
                         q.where("id", 3).orWhere("age", 2);
                     })
                     .when(test2, (q) -> {
                         q.where("id", 4).where("age", 1);
                     })
                ;
            })

        ;

        var result = builder.compile();
        System.out.println(result);

        selector.replaceSelect()
            .selectRaw("count(*) as aggregate");

        result = builder.compile();
        System.out.println(result);
        selector.recoverSelect();
        result = builder.compile();
        System.out.println(result);




        var res = builder.select((stmt) -> {
            stmt.from("user")
                .select("id", "name", "age")
                .where((query) -> {
                    query.where("id", 1)
                         .whereIn("name", "haha", "baba")
                         .when(test1, (q) -> {
                             q.where("id", 3);
                         })
                         .when(test2, (q) -> {
                             q.where("id", 4);
                         })
                    ;
                })

            ;
        }).compile();

        System.out.println(res);
    }

}
