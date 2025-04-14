package com.lyhux.mybatiscrud.builder.test;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;
import com.lyhux.mybatiscrud.builder.vendor.Grammar;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UpdateTest {
    static final Grammar mysqlGrammar = new MysqlGrammar();

    @Test
    public void testAssign() {
        var assign = new AssignListExpr();
        assign.set(new AssignExpr<>(new EscapedStr("column"), new BindingValue<>(new EscapedStr("value"))));

        assign.set(new AssignExpr<>(
                new EscapedStr("column"),
                new BindingValue<>(new RawStr("?"), TypeValue.of(1234))));

        // exprAssert(assign);
        G.assertEquals(
            mysqlGrammar,
            assign,
            "`column`='value', `column`=?"
        );
    }

    @Test
    public void testSet() {
        var update = new Query()
                .table("users")
        // update
                .set((set) -> {
                    set
                            .set("id", 1)
                            .set("name", "lyhux")
                            .setRaw("email", "hello@gmail.com")
                    ;

                })
            .toUpdateStmt()
        ;

        G.assertEquals(
            mysqlGrammar,
            update,
            "UPDATE `users` SET `id`=?, `name`=?, `email`='hello@gmail.com'"
        );
    }

    @Test
    public void testUpdateWithJoin() {
        var update = new Query()
            .table("users")
            .join("orders", wrapper -> wrapper.on("order.user_id", "=", "user.id"))
            // update
            .set((set) -> {
                set
                    .setRaw("orders.username", "users.username")
                ;

            })
            .toUpdateStmt()
            ;

        G.assertEquals(
            mysqlGrammar,
            update,
            "UPDATE `users` INNER JOIN `orders` ON `order`.`user_id` = `user`.`id` SET orders.username=users.username"
        );
    }

    @Test
    public void testWhere() {
        var update = new Query()
            .table("users")
            .set((set) -> {
                set
                    .set("id", 1)
                    .set("name", "lyhux")
                    .setRaw("email", "hello@gmail.com")
                ;

            })
            .where((where) -> {
                where.where("id", 1);
            })
            .limit(10, 5)
            .toUpdateStmt()
            ;

        G.assertEquals(
            mysqlGrammar,
            update,
            "UPDATE `users` SET `id`=?, `name`=?, `email`='hello@gmail.com' WHERE `id` = ? LIMIT 10 OFFSET 5",
            List.of(
                TypeValue.of(1),
                TypeValue.of("lyhux"),
                TypeValue.of(1)
            )
        );
    }
}
