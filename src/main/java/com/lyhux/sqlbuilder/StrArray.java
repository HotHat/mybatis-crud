package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class StrArray extends ArrayList<String> {
    public static StrArray asList(String... strings) {
        StrArray strArray = new StrArray();
        strArray.addAll(Arrays.asList(strings));
        return strArray;
    }
}
