package com.lyhux.sqlbuilder.grammar;

import com.lyhux.sqlbuilder.grammar.update.AssignExpr;
import com.lyhux.sqlbuilder.grammar.update.AssignListExpr;
import com.mysql.cj.xdevapi.UpdateStatement;
import org.junit.jupiter.api.Test;

public class UpdateTest extends MysqlGrammarTest {
    @Test
    public void testAssign() {
        var assign = new AssignListExpr();
        assign.add(new AssignExpr<>(new EscapedStr("column"), new BindingValue<>(new EscapedStr("value"))));

        assign.add(new AssignExpr<>(
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
                            .add("id", 1)
                            .add("name", "lyhux")
                            .addRaw("email", "hello@gmail.com")
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
                            .add("id", 1)
                            .add("name", "lyhux")
                            .addRaw("email", "hello@gmail.com")
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
