package test.java.grammar;

import main.java.com.lyhux.sqlbuilder.grammar.EscapedStr;
import main.java.com.lyhux.sqlbuilder.grammar.RawStr;
import main.java.com.lyhux.sqlbuilder.grammar.SelectExpr;
import org.junit.jupiter.api.Test;

public class SelectExprTest extends MysqlTest {

    @Test
    public void testSelectExpr() {
        var expr = new SelectExpr();
        expr.add(new RawStr("version()"))
                .add(new EscapedStr("id AS user_id"))
                .add(new EscapedStr("orders.id AS order_id"))
        ;

        print(expr);
    }


}
