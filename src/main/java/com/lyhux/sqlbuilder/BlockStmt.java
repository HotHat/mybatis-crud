package main.java.com.lyhux.sqlbuilder;

public interface BlockStmt {
    public boolean isBuilder();
    public boolean isRaw();

    public CompileResult compile();
}

