package test.java.grammar;

import com.lyhux.sqlbuilder.grammar.*;
import com.lyhux.sqlbuilder.vendor.MysqlCompiler;
import org.junit.jupiter.api.BeforeEach;

public class MysqlTest {
    protected MysqlCompiler compiler;


    @BeforeEach
    public void setUp() throws Exception {
        compiler = new MysqlCompiler();
    }

    public void print(SelectExpr expr) {
        var result = compiler.compile(expr);
        System.out.println(result);
    }

    public void print(WhereClauseExpr expr) {
        var result = compiler.compile(expr);
        System.out.println(result.sql());
        System.out.println(result.bindings());
    }

    public void print(ExprResult result) {
        System.out.println(result.sql());
        System.out.println(result.bindings());
    }

    public void print(TableNameExpr expr) {
        var result = compiler.compile(expr);
        System.out.println(result);
    }

    public void print(TableSubExpr expr) {
        var result = compiler.compile(expr);
        System.out.println(result.sql());
        System.out.println(result.bindings());
    }

    public void print(TableFactorExpr expr) {
        var result = compiler.compile(expr);
        System.out.println(result.sql());
        System.out.println(result.bindings());
    }

    public void print(TableJoinedExpr expr) {
        var result = compiler.compile(expr);
        System.out.println(result.sql());
        System.out.println(result.bindings());
    }

    public void print(TableRefExpr expr) {
        var result = compiler.compile(expr);
        System.out.println(result.sql());
        System.out.println(result.bindings());
    }

    public void print(TableRefsExpr expr) {
        var result = compiler.compile(expr);
        System.out.println(result.sql());
        System.out.println(result.bindings());
    }

    public void print(SelectStmt stmt) {
        var result = compiler.compile(stmt);
        System.out.println(result.sql());
        System.out.println(result.bindings());
    }

}
