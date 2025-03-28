package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignListExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueGroupExpr;
import org.junit.jupiter.api.Test;

public class InsertTest extends MysqlGrammarTest {
    @Test
    public void testValueExpr() {
        var val = new BindingValue<>(new RawStr("23"));
        var val1 = new BindingValue<>(new RawStr("?"), TypeValue.of(23));

        System.out.printf("value: %s, val1: %s\n", val, val1);
    }

    @Test
    public void testValueListExpr() {
        var lst = new ValueGroupExpr();

        var val = new BindingValue<>(new EscapedStr("23"));
        var val1 = new BindingValue<>(new RawStr("?"), TypeValue.of(23));

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
        var lst = new DuplicateAssignListExpr();

        lst.add(new DuplicateAssignExpr(new EscapedStr("name"), new EscapedStr("ab.name")));
        lst.add(new DuplicateAssignExpr(new EscapedStr("name2"), new EscapedStr("ab.name2")));

        print(lst);
    }

    @Test
    public void testInsertExpr() {
        var builder = new Query();

        // var insert = new InsertStmt(new EscapedStr("users"));
        builder
                .columns("id", "name", "age", "gender")
                .values((group) -> {
                    group
                            .add("1")
                            .add("name1")
                            .add("12")
                            .add("male")
                    ;
                })
                .values((group) -> {
                    group
                            .add(2)
                            .addRaw("raw name")
                            .add(15)
                            .add("female")
                    ;
                })

                .onUpdate((update) -> {
                    update
                            .add("a1", "b1")
                            .add("a1", "b1")
                            .add("a1", "b1");
                });


        var insert = builder.toInsertStmt();

        print(insert);

    }
}
