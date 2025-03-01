package main.java.com.lyhux.sqlbuilder.grammar;

import java.util.ArrayList;
import java.util.List;

public class SelectExpr {
    List<ExprStr> select;

    public List<ExprStr> getSelect() {
        return select;
    }

    public SelectExpr add(ExprStr expr) {
        select.add(expr);
        return this;
    }

    public SelectExpr() {
        select = new ArrayList<ExprStr>();
    }

    public SelectExpr(List<ExprStr> select) {
        this.select = select;
    }
}
