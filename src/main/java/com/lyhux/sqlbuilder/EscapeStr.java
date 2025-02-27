package main.java.com.lyhux.sqlbuilder;

public class EscapeStr extends RawStr {

    public EscapeStr(String stmt) {
        super(stmt);
    }

    @Override
    public boolean isBuilder() {
        return false;
    }

    @Override
    public boolean isRaw() {
        return false;
    }

    @Override
    public CompileResult compile() {
        return new CompileResult(stmt);
    }

}
