package main.java.com.lyhux.sqlbuilder;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


// class WhereNestStmt extends WhereBlock{
//     public WhereNestStmt(WhereJoin join, WhereStmt group) {
//         super(join, group);
//     }
//
//     @Override
//     public CompileResult compile() {
//         var s = new StringBuilder();
//         List<StmtValue<?>> values = new ArrayList<>();
//
//         s.append("(");
//         for (int i = 0; i < group.blocks.size(); i++) {
//             var current = group.blocks.get(i);
//             if (i == 0) {
//                 current.setHiddenJoin(true);
//             }
//             CompileResult r = current.compile();
//             s.append(r.getSqlStmt());
//             values.addAll(r.getParameter().getValues());
//         }
//         s.append(")");
//
//         return new CompileResult(s.toString(), new StmtParameter(values));
//     }
// }


public class Builder implements Stmt {

    protected SelectStmt selectStmt;

    protected List<Table> from;


    protected List<JoinClause> join;

    protected WhereStmt whereRoot;
    protected WhereStmt wheres;
//    private Connection connection;


    public Builder() {
        selectStmt = new SelectStmt();
        from = new ArrayList<>();

        whereRoot = new WhereStmt();
        wheres = whereRoot;
        // wheres = new WhereStmt();

        join = new ArrayList<>();
    }

    public SelectStmt getSelectStmt() {
        return selectStmt;
    }

    public List<Table> getFrom() {
        return from;
    }

    public List<JoinClause> getJoin() {
        return join;
    }

    public WhereStmt getWhereRoot() {
        return whereRoot;
    }

    public WhereStmt getWheres() {
        return wheres;
    }


    public Builder from(String table) {
        return from(table, "");
    }

    public Builder from(String table, String as) {
        from.add(new Table(table, as));
        return this;
    }

    public Builder select(String... fields) {
        selectStmt.addAll(Arrays.stream(fields).map(EscapeStr::new).toList());
        return this;
    }

    protected  <T> Builder baseWhere(String column, String operator, T value, CustomType type, boolean isAnd, boolean isRawStr) {
        wheres.add(new WhereCompare(
                    isRawStr ? RawStr.of(column) : EscapeStr.of(column),
                    operator,
                    new StmtValue<>(type, value)
                ),
                isAnd
        );
        return this;
    }

    public Builder where(String column, String value) {
        return baseWhere(column, "=", value, CustomType.ofType(JDBCType.VARCHAR), true, false);
    }

    public Builder where(String column, String operator, String value) {
        return baseWhere(column, operator, value, CustomType.ofType(JDBCType.VARCHAR), true, false);
    }

    public Builder orWhere(String column, String value) {
        return baseWhere(column, "=", value,  CustomType.ofType(JDBCType.VARCHAR), false, false);
    }

    public Builder where(RawStr rawStmt, IntArray values) {
        return baseWhere(rawStmt.getStmt(), "", values,  CustomType.ofType(BuilderType.INTEGER_ARRAY), true, true);
    }

    public Builder where(WhereQuery query) {
        var preWhere = wheres;
        wheres = new WhereStmt();
        preWhere.add(wheres, true);

        query.where(this);
        wheres = preWhere;
        return this;
    }

    public Builder whereIn(String column, StrArray values) {
        return baseWhere(column, "IN", values,  CustomType.ofType(BuilderType.STRING_ARRAY), true, false);
    }

    public Builder whereIn(String column, IntArray values) {

        wheres.add(
                new WhereCompare(EscapeStr.of(column), "IN",
                        new StmtValue<>(CustomType.ofType(BuilderType.INTEGER_ARRAY), values)), true);

        return this;
    }

    public Builder whereIn(String column, Builder subQuery) {

        wheres.add(
                new WhereCompare(EscapeStr.of(column), "IN",
                new StmtValue<>(CustomType.ofType(BuilderType.BUILDER), subQuery)), true);

        return this;
    }

    public Builder join(String table, String leftKey, String op, String rightKey) {
        join.add(new JoinClause("INNER", table, leftKey, op, rightKey));
        return this;
    }

    public Builder join(String table, JoinQuery join) {
        var joinClause = new JoinClause("INNER", table);
        join.join(joinClause);
        this.join.add(joinClause);
        return this;
    }


    public Builder leftJoin(String table, String leftKey, String op, String rightKey) {
        join.add(new JoinClause("LEFT", table, leftKey, op, rightKey));
        return this;
    }

    public Builder rightJoin(String table, String leftKey, String op, String rightKey) {
        join.add(new JoinClause("RIGHT", table, leftKey, op, rightKey));
        return this;
    }

    @Override
    public boolean isBuilder() {
        return true;
    }

    @Override
    public boolean isRaw() {
        return false;
    }

    @Override
    public CompileResult compile() {
        // StringBuilder s = new StringBuilder();
        // List<StmtValue<?>>  values = new LinkedList<>();
        //
        // if (!selectStmt.isEmpty()) {
        //     CompileResult r = Utils.stmtJoin(selectStmt, ", ");
        //     s.append(" SELECT ").append(r.getSqlStmt());
        // } else {
        //     s.append(" SELECT * ");
        // }
        //
        // if (!from.isEmpty()) {
        //     CompileResult r = Utils.stmtJoin(from, ", ");
        //     s.append(" FROM ").append(r.getSqlStmt());
        //     values.addAll(r.getParameter().getValues());
        // }
        //
        // if (!join.isEmpty()) {
        //     CompileResult r = Utils.stmtJoin(join, " ");
        //     s.append(" ").append(r.getSqlStmt());
        //     values.addAll(r.getParameter().getValues());
        // }
        //
        // if (wheres.isNotEmpty()) {
        //     CompileResult r = whereRoot.compile();
        //     s.append(" WHERE ").append(r.getSqlStmt());
        //     values.addAll(r.getParameter().getValues());
        // }
        //
        // return new CompileResult(s.toString(), new StmtParameter(values));
        return null;
    }
}
