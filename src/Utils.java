import java.util.List;


public class Utils {
    public static String compileJoin(List<? extends SqlClauseCompiler> arr, String sep) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < arr.size(); i++ ) {
            s.append(arr.get(i).compile());
            if (i < arr.size() - 1) {
                s.append(sep);
            }
        }
        return s.toString();
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
