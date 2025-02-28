package main.java.com.lyhux.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class WhereStmt extends AbstractStmt {
    protected List<WhereBlock> blocks;

    public WhereStmt() {
        blocks = new ArrayList<>();
    }

    public boolean isGroup() { return true; }
    public boolean isNotEmpty() { return !blocks.isEmpty(); }
    public int size() { return blocks.size(); }

    public void add(WhereStmt group, boolean isAnd) {
        blocks.add(new WhereBlock(isAnd ? WhereJoin.AND : WhereJoin.OR, group));
    }

    @Override
    public CompileResult compile() {
        var s = new StringBuilder();
        List<StmtValue<?>> values = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            var current = blocks.get(i);
            if (i == 0) {
                current.setHiddenJoin(true);
            }
            CompileResult r = current.compile();
            s.append(r.getSqlStmt());
            values.addAll(r.getParameter().getValues());
        }

        return new CompileResult(s.toString(), new StmtParameter(values));
    }
}