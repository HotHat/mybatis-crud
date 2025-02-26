package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class CompileResult {
    private final String sqlStmt;
    private final List<StmtValue<?>> parameter;

    public CompileResult(String sqlStmt, List<StmtValue<?>> parameter) {
        this.sqlStmt = sqlStmt;
        this.parameter = parameter;
    }

    public CompileResult(String sqlStmt) {
        this.sqlStmt = sqlStmt;
        this.parameter = new ArrayList<StmtValue<?>>();
    }

    public String getSqlStmt() {
        return sqlStmt;
    }

    public List<StmtValue<?>> getParameter() {
        return parameter;
    }

}
