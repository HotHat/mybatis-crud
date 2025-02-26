package main.java.com.lyhux.sqlbuilder;

public abstract class StmtCompile implements BlockStmt {
    @Override
    public boolean isBuilder() {
        return false;
    }

}
