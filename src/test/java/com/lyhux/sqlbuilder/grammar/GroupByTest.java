package com.lyhux.sqlbuilder.grammar;

import org.junit.jupiter.api.Test;

import java.util.List;

public class GroupByTest extends MysqlGrammarTest {
    @Test
    public void testGroupBy(){
        var groupBy = new GroupByExpr();

        groupBy.groupBy(new EscapedStr("role"))
                .groupBy(new EscapedStr("type"))
                .having((query) -> {
                    query.where("role", ">", 3);
                })
        ;

        print(groupBy);
    }
}
