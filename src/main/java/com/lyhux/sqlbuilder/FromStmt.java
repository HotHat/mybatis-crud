package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class FromStmt {
    private final List<Table> tables;

    public FromStmt() {
        tables = new ArrayList<Table>();
    }

    public List<?> getTables() {
        return tables;
    }

    public void addTable(Table table) {
        tables.add(table);
    }
}
