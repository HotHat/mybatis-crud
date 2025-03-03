package com.lyhux.sqlbuilder.grammar;

import com.lyhux.sqlbuilder.grammar.select.GroupByExpr;
import org.junit.jupiter.api.Test;

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
