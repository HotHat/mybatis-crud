package com.lyhux.mybatiscrud.builder.grammar;

import java.util.ArrayList;
import java.util.List;

public class UnionClause {
    List<UnionItem> unionItems;
    public UnionClause() {
        unionItems = new ArrayList<>();
    }

    public boolean isEmpty() { return unionItems.isEmpty(); }
    public void add(UnionItem item) {
        unionItems.add(item);
    }

    public List<UnionItem> getUnionItems() { return unionItems; }
}
