package test.java.grammar;

import main.java.com.lyhux.sqlbuilder.grammar.SelectStmt;
import org.junit.jupiter.api.Test;

public class SelectStmtTest extends MysqlTest {
    @Test
    public void testSimpleSelectStmt() {
        var builder = new SelectStmt();

        builder.select("*")
                .from("users")
                .where((query) -> {
                    query.where("id", 123)
                            .where("username", "test")
                            ;
                })
        ;

        print(builder);
    }

    @Test
    public void testSubQuerySelectStmt() {
        var builder = new SelectStmt();

        var table = new SelectStmt();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table, "tb1")
        ;

        print(builder);
    }

    @Test
    public void testJoinSelectStmt() {
        var builder = new SelectStmt();

        var table = new SelectStmt();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from("users")
                .join("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder);
    }
    @Test
    public void testSubQueryWithJoinSelectStmt() {
        var builder = new SelectStmt();

        var table = new SelectStmt();
        table.select("*").from("users").where((query) -> { query.where("id", ">", 123);});

        builder.select("*")
                .from(table, "tb1")
                .join("orders", "users.id", "=", "orders.user_id")
        ;

        print(builder);
    }
}
