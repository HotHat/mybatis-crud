package main.java.com.lyhux.sqlbuilder;

public abstract class AbstractStmt implements Stmt {
    @Override
    public boolean isBuilder() {
        return false;
    }

    @Override
    public boolean isRaw() {
        return false;
    }

}
