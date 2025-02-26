package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.List;


public class Utils {
    public static String compileJoin(List<? extends StmtCompile> arr, String sep) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < arr.size(); i++ ) {
            s.append(arr.get(i).compile());
            if (i < arr.size() - 1) {
                s.append(sep);
            }
        }
        return s.toString();
    }

    public static CompileResult stmtJoin(List<? extends StmtCompile> arr, String sep) {
        StringBuilder s = new StringBuilder();
        List<StmtValue> values = new ArrayList<StmtValue>();

        for (int i = 0; i < arr.size(); i++ ) {
            CompileResult r = arr.get(i).compile();
            s.append(r.getSqlStmt());
            if (i < arr.size() - 1) {
                s.append(sep);
            }
            values.addAll(r.getParameter());
        }
        return new CompileResult(s.toString(), values);
    }

    public static String strJoin(List<String> arr, String sep) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < arr.size(); i++ ) {
            s.append(arr.get(i));
            if (i < arr.size() - 1) {
                s.append(sep);
            }
        }
        return s.toString();
    }
}
