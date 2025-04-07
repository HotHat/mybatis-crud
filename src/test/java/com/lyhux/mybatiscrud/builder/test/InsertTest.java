package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignListExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueGroupExpr;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        exprAssert(lst);
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

        exprAssert(columnExpr, null);
    }

    @Test
    public void testAssignListExpr() {
        var lst = new DuplicateAssignListExpr();

        lst.add(new DuplicateAssignExpr(new EscapedStr("name"), new BindingValue<>(new EscapedStr("ab.name"))));
        lst.add(new DuplicateAssignExpr(new EscapedStr("name2"), new BindingValue<>(new EscapedStr("ab.name2"))));

        exprAssert(lst);
    }

    @Test
    public void testInsertExpr() {
        var query = new Query();

        // var insert = new InsertStmt(new EscapedStr("users"));
        query
                .table("users")
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
                        .addRaw("a1", "a1+1")
                        .addColumn("b1", "b1")
                        .addColumn("c1", "c1")
                        .add("r1", "888")
                    ;
                });


        var insert = query.toInsertStmt();

        exprAssert(insert,
            new ExprResult(
                "INSERT INTO `users` (`id`, `name`, `age`, `gender`) VALUES (?, ?, ?, ?), (?, 'raw name', ?, ?) ON DUPLICATE KEY UPDATE `a1`=a1+1, `b1`=`b1`, `c1`=`c1`, `r1`=?",
                List.of(
                    TypeValue.of("1"),
                    TypeValue.of("name1"),
                    TypeValue.of("12"),
                    TypeValue.of("male"),
                    TypeValue.of(2),
                    TypeValue.of(15),
                    TypeValue.of("female"),
                    TypeValue.of("888")
                )
            )
        );

    }
}
