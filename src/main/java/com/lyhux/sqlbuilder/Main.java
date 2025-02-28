package main.java.com.lyhux.sqlbuilder;


import java.sql.JDBCType;

class Cast {
    public StmtValue<?> toStr(String val) {
        return new StmtValue<>(CustomType.ofType(JDBCType.VARCHAR), val);
    }

    public StmtValue<?> toStr(int val) {
        return new StmtValue<>(CustomType.ofType(JDBCType.INTEGER), val);
    }
}

public class Main {

    // public <T> StmtValue<?> cast(T value) {
        // return toStr(value);
    // }



    public static void main(String[] args) {
        Main m = new Main();

        var cast = new Cast();

        var a = cast.toStr(123);
        var b = cast.toStr("abc");


    }
}