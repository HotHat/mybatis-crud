package main.java.com.lyhux.sqlbuilder;

public class RawStmt implements BlockStmt {
    final String stmt;

    public String getStmt() {
        return stmt;
    }

    public RawStmt(String stmt) {
        this.stmt = stmt;
    }

    @Override
    public boolean isBuilder() {
        return false;
    }

    @Override
    public CompileResult compile() {
        return new CompileResult(stmt);
    }

}
