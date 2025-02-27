package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class CompileResult {
    private final String sqlStmt;
    private final StmtParameter parameter;

    public CompileResult(String sqlStmt, StmtParameter parameter) {
        this.sqlStmt = sqlStmt;
        this.parameter = parameter;
    }

    public CompileResult(String sqlStmt) {
        this.sqlStmt = sqlStmt;
        this.parameter = new StmtParameter();
    }

    public String getSqlStmt() {
        return sqlStmt;
    }

    public StmtParameter getParameter() {
        return parameter;
    }

}
