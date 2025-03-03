package com.lyhux.sqlbuilder.grammar;

import com.lyhux.sqlbuilder.grammar.insert.*;
import org.junit.jupiter.api.Test;

public class InsertTest extends MysqlGrammarTest {
    @Test
    public void testValueExpr() {
        var val = new ValueExpr<>(new RawStr("23"));
        var val1 = new ValueExpr<>(new RawStr("?"), ExprValue.of(23));

        System.out.printf("value: %s, val1: %s\n", val, val1);
    }

    @Test
    public void testValueListExpr() {
        var lst = new ValueGroupExpr();

        var val = new ValueExpr<>(new EscapedStr("23"));
        var val1 = new ValueExpr<>(new RawStr("?"), ExprValue.of(23));

        lst.add(val);
        lst.add(val1);

        print(lst);
    }

    @Test
    public void testValuesExpr() {}

    @Test
    public void testColumnExpr() {
        var columnExpr = new ColumnExpr();
        columnExpr
                .add(new EscapedStr("id"))
                .add(new EscapedStr("name"))
                .add(new EscapedStr("age"))
                .add(new EscapedStr("gender"));

        print(columnExpr);
    }

    @Test
    public void testAssignListExpr() {
        var lst = new AssignListExpr();

        lst.add(new AssignExpr(new EscapedStr("name"), new EscapedStr("ab.name")));
        lst.add(new AssignExpr(new EscapedStr("name2"), new EscapedStr("ab.name2")));

        print(lst);
    }

    @Test
    public void testInsertExpr() {
        var insert = new InsertStmt(new EscapedStr("users"));
        insert
                .columns("id", "name", "age", "gender")
                .values((group) -> {
                    group
                            .add(new ValueExpr<>(new EscapedStr("1")))
                            .add(new ValueExpr<>(new RawStr("?"), ExprValue.of("name1")))
                            .add(new ValueExpr<>(new EscapedStr("12")))
                            .add(new ValueExpr<>(new EscapedStr("male")))
                    ;
                })
                .values((group) -> {
                    group
                            .add(new ValueExpr<>(new EscapedStr("2")))
                            .add(new ValueExpr<>(new RawStr("?"), ExprValue.of("string value")))
                            .add(new ValueExpr<>(new RawStr("?"), ExprValue.of(15)))
                            .add(new ValueExpr<>(new EscapedStr("female")))
                    ;
                })

                .onUpdate((update) -> {
                    update
                            .add("a1", "b1")
                            .add("a1", "b1")
                            .add("a1", "b1");
                });
        ;

        print(insert);

    }
}
