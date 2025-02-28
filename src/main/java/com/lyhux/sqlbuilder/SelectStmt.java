package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class SelectStmt {
    private final List<? super RawStr> fields;
    public SelectStmt() {
        fields = new ArrayList<RawStr>();
    }

    public List<?> getFields() {
        return fields;
    }

    public void addField(RawStr field) {
        fields.add(field);
    }

    public void addAll(List<? extends RawStr> fields) {
        this.fields.addAll(fields);
    }
}
