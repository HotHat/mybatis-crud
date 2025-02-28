package main.java.com.lyhux.sqlbuilder;

public class Table extends AbstractStmt {
    public String tableName;
    public String asName;

    public Table(String tableName) {
        this.tableName = tableName;
        this.asName = "";
    }
    public Table(String tableName, String asName) {
        this.tableName = tableName;
        this.asName = asName;
    }

    @Override
    public CompileResult compile() {
        if (asName.isEmpty()) {
            return new CompileResult(tableName);
        } else {
            return new CompileResult(tableName + " AS " + asName);
        }
    }
}