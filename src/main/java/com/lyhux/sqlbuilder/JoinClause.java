package main.java.com.lyhux.sqlbuilder;

import java.sql.JDBCType;
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
        baseWhere(leftKey, operator, rightKey, CustomType.ofType(BuilderType.RAW_STRING), true, true);
        return this;
    }

    @Override
    public CompileResult compile() {

        StringBuilder s = new StringBuilder();
        List<StmtValue<?>> values = new LinkedList<>();

        s.append(type);
        s.append(" JOIN ");

        if (wheres.isNotEmpty()) {
            s.append(" ON ");
            if (wheres.size() > 1) {
                s.append("(");
            }
            CompileResult r = wheres.compile();
            s.append(r.getSqlStmt());
            if (wheres.size() > 1) {
                s.append(")");
            }

            values.addAll(r.getParameter().getValues());
        }

        return new CompileResult(s.toString(), new StmtParameter(values));
    }
}
