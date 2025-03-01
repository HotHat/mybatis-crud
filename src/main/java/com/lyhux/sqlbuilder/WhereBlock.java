package main.java.com.lyhux.sqlbuilder;

/**
 * (a b ((c d) e (f (g h) i)) j k)
 */
public class WhereBlock extends AbstractStmt {
    public WhereJoin join;
    public WhereStmt whereStmt;

    private boolean hiddenJoin = false;

    public void setHiddenJoin(boolean hiddenJoin) {
        this.hiddenJoin = hiddenJoin;
    }

    public WhereBlock(WhereJoin join, WhereStmt whereStmt) {
        this.join = join;
        this.whereStmt = whereStmt;
    }

    @Override
    public CompileResult compile() {
        StringBuilder s = new StringBuilder();

        CompileResult r = whereStmt.compile();
        StmtParameter values = new StmtParameter(r.getParameter().getValues());

        if (hiddenJoin) {
            if (whereStmt.isGroup()) {
                s.append(" (");
                s.append(r.getSqlStmt());
                s.append(" )");

            } else {
                s.append(r.getSqlStmt());
            }
        } else {
            if (whereStmt.isGroup()) {
                s.append(" ")
                        .append(join.getName())
                        .append(" (")
                        .append(r.getSqlStmt())
                        .append(")");
            } else {
                s.append(" ")
                        .append(join.getName())
                        .append(" ")
                        .append(r.getSqlStmt());
            }
        }
        return new CompileResult(s.toString(), values);
    }
}