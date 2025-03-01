package main.java.com.lyhux.sqlbuilder.grammar;

import main.java.com.lyhux.sqlbuilder.RawStr;

import java.util.ArrayList;
import java.util.List;

public class SelectExpr {
    List<ExprStr> select;

    public List<ExprStr> getSelect() {
        return select;
    }

    public boolean isEmpty() { return select.isEmpty(); }

    public SelectExpr add(ExprStr expr) {
        select.add(expr);
        return this;
    }

    public void addAll(List<? extends ExprStr> fields) {
        this.select.addAll(fields);
    }

    public SelectExpr() {
        select = new ArrayList<ExprStr>();
    }

    public SelectExpr(List<ExprStr> select) {
        this.select = select;
    }
}
