package main.java.com.lyhux.sqlbuilder;

import java.sql.JDBCType;
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

    public static CompileResult stmtJoin(List<?> arr, String sep) {
        StringBuilder s = new StringBuilder();
        List<StmtValue<?>> values = new ArrayList<>();

        for (int i = 0; i < arr.size(); i++ ) {
            var target = castToType(arr.get(i), BlockStmt.class);
            assert target != null;
            CompileResult r = target.compile();
            s.append(r.getSqlStmt());
            if (i < arr.size() - 1) {
                s.append(sep);
            }
            values.addAll(r.getParameter().getValues());
        }
        return new CompileResult(s.toString(), new StmtParameter(values));
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

    public static String toMark(StmtValue<?> value, String sep) {
        if (value.type().isJDBCType() && value.type().getJDBCType() == JDBCType.ARRAY) {
            StringBuilder s = new StringBuilder();
            List<?> val = castToType(value.value(), List.class);
            for (int i = 0; i < val.size(); i++ ) {
                s.append("?");
                if (i < val.size() - 1) {
                    s.append(sep);
                }
            }
            return s.toString();
        } else {
            return value.value().toString();
        }

    }

    public static <T> T castToType(Object obj, Class<T> clazz) {
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }
        return null; // Or throw an exception if the cast is not possible
    }

}
