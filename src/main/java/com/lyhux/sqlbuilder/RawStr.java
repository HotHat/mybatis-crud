package main.java.com.lyhux.sqlbuilder;

public class RawStr implements BlockStmt {
    protected String stmt;

    public String getStmt() {
        return stmt;
    }

    public RawStr(String stmt) {
        this.stmt = stmt;
    }

    @Override
    public boolean isBuilder() {
        return false;
    }

    @Override
    public boolean isRaw() {
        return true;
    }

    @Override
    public CompileResult compile() {
        return new CompileResult(stmt);
    }

    public static RawStr of(String stmt) {
        return new RawStr(stmt);
    }
}
