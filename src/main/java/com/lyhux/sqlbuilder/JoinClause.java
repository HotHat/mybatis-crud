package main.java.com.lyhux.sqlbuilder;

import java.util.LinkedList;
import java.util.List;

public class JoinClause extends Builder {
    private String type;

    public JoinClause(String type, String tableName) {
        this.type = type;
        from(tableName);
    }

    public JoinClause(String type, String tableName, String firstKey, String operator, String secondKey) {
        this.type = type;
        from(tableName);
        where(firstKey, operator, secondKey);
    }

    public JoinClause on(String leftKey, String operator, String rightKey) {
        where(leftKey, operator, rightKey);
        return this;
    }

    @Override
    public CompileResult compile() {

        StringBuilder s = new StringBuilder();
        List<StmtValue<?>> values = new LinkedList<>();

        s.append(type);
        s.append(" JOIN ");

        if (wheres.isNotEmpty()) {
            CompileResult r = wheres.compile();
            s.append(" ON ").append(r.getSqlStmt());
            values.addAll(r.getParameter().getValues());
        }

        return new CompileResult(s.toString(), new StmtParameter(values));
    }
}
