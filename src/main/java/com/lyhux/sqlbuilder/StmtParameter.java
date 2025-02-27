package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StmtParameter {
    private final List<StmtValue<?>> values;

    public List<StmtValue<?>> getValues() {
        return values;
    }

    public StmtParameter() {
        this.values = new ArrayList<StmtValue<?>>();
    }

    public StmtParameter(List<StmtValue<?>> values) {
        this.values = values;
    }

    public StmtParameter(StmtValue<?> values) {
        this.values = Collections.singletonList(values);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (StmtValue<?> value : values) {
            count++;
            sb.append(value.toString());
            if (count < values.size()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
