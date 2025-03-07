package com.lyhux.mybatiscrud.builder.grammar;

import com.lyhux.mybatiscrud.builder.grammar.update.AssignExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;
import org.junit.jupiter.api.Test;

public class UpdateTest extends MysqlGrammarTest {
    @Test
    public void testAssign() {
        var assign = new AssignListExpr();
        assign.set(new AssignExpr<>(new EscapedStr("column"), new BindingValue<>(new EscapedStr("value"))));

        assign.set(new AssignExpr<>(
                new EscapedStr("column"),
                new BindingValue<>(new RawStr("?"), TypeValue.of(1234))));

        print(assign);
    }

    @Test
    public void testSet() {
        var update = new UpdateStmt("users");
        update
                .set((set) -> {
                    set
                            .set("id", 1)
                            .set("name", "lyhux")
                            .setRaw("email", "hello@gmail.com")
                    ;

                })
        ;

       print(update);
    }

    @Test
    public void testWhere() {
        var update = new UpdateStmt("users");
        update
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
        ;

        print(update);
    }
}
