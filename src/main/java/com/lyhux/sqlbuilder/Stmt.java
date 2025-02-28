package main.java.com.lyhux.sqlbuilder;

public interface Stmt {
    public boolean isBuilder();
    public boolean isRaw();

    public CompileResult compile();
}

