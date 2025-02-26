package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class IntArray extends ArrayList<Integer> {
    public static IntArray asList(Integer... values) {
        IntArray array = new IntArray();
        array.addAll(Arrays.asList(values));
        return array;
    }
}
