package main.java.com.lyhux.sqlbuilder.grammar;

public final class TableNameExpr implements TableFactorExpr{
    String tableName;
    String alias = "";

    public TableNameExpr(String tableName) {
        this.tableName = tableName;
        alias = "";
    }

    public TableNameExpr(String tableName, String alias) {
        this.tableName = tableName;
        this.alias = alias;
    }

    public String getTableName() { return tableName; }
    public String getAlias() { return alias; }
}
