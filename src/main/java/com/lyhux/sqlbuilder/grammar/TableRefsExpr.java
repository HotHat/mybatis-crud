package main.java.com.lyhux.sqlbuilder.grammar;

import java.util.ArrayList;
import java.util.List;

public class TableRefsExpr implements Expr {
    List<TableRefExpr> tableRefs;

    public TableRefsExpr() {
        tableRefs = new ArrayList<TableRefExpr>();
    }

    public TableRefsExpr(TableRefExpr ref) {
        tableRefs = new ArrayList<TableRefExpr>();
        tableRefs.add(ref);
    }

    public TableRefsExpr add(TableRefExpr ref) {
        this.tableRefs.add(ref);
        return this;
    }

    public List<TableRefExpr> getTableRefs() { return this.tableRefs; }

}
