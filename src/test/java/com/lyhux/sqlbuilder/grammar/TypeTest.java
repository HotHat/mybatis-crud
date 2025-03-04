package com.lyhux.sqlbuilder.grammar;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;


record Type<T>(T value) {

}

class AllType {
    public List<Type<?>> types;
}

public class TypeTest {
    @Test
    public void testType() {
        var t1 = new Type<>("String");
        var t2 = new Type<>(1234);
        var t3 = new Type<>(LocalDateTime.now());

        var all = new AllType();
        all.types = List.of(t1, t2, t3);
        for (var t : all.types) {
            System.out.printf("%s, %s\n", t.value().getClass().getSimpleName(), t.toString());
        }

    }

    private void print(String... s) {
        printT(s);
    }

    private void print(Integer... s) {
        printT(s);
    }

    public <T> void printT(T[] list) {
        for (var t : list) {
            System.out.println(t);;
        }
    }

    public <T> void printT(List<T> list) {
        for (var t : list) {
            System.out.println(t);;
        }
    }

    @Test
    public void testPrint() {
        printT(List.of("a", "b", "c"));
        print(new String[]{"a2", "b1", "c1"});
        print(new Integer[]{1, 2, 3});
    }

}
