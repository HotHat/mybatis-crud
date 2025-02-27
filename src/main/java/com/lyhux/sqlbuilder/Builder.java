package main.java.com.lyhux.sqlbuilder;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Table extends StmtCompile {
    public String tableName;
    public String asName;

    public Table(String tableName) {
        this.tableName = tableName;
        this.asName = "";
    }
    public Table(String tableName, String asName) {
        this.tableName = tableName;
        this.asName = asName;
    }

    @Override
    public CompileResult compile() {
        if (asName.isEmpty()) {
            return new CompileResult(tableName);
        } else {
            return new CompileResult(tableName + " AS " + asName);
        }
    }
}

/**
 * (a b ((c d) e (f (g h) i)) j k)
 */
class WhereBlock extends StmtCompile {
    public WhereJoin join;
    public WhereStmt group;

    private boolean hiddenJoin = false;


    public void setHiddenJoin(boolean hiddenJoin) {
        this.hiddenJoin = hiddenJoin;
    }

    public WhereBlock(WhereJoin join, WhereStmt group) {
        this.join = join;
        this.group = group;
    }

    @Override
    public CompileResult compile() {
        StringBuilder s = new StringBuilder();

        CompileResult r = group.compile();
        StmtParameter values = new StmtParameter(r.getParameter().getValues());

        if (hiddenJoin) {
            if (group.isGroup()) {
                s.append(" (");
                s.append(r.getSqlStmt());
                s.append(" )");

            } else {
                s.append(r.getSqlStmt());
            }
        } else {
            if (group.isGroup()) {
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

class WhereCompare extends WhereStmt {
    public RawStr columnName;
    public String operator;
    public StmtValue<?> value;

    public boolean isGroup() { return false; }

    WhereCompare(RawStr columnName, String operator, StmtValue<?> value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public CompileResult compile() {
            List<StmtValue<?>> val = new LinkedList<>();
            StringBuilder s = new StringBuilder();
            // IN (?, ?, ?)
            if (value.type().isJDBCType()) {
                val.add(value);
            }
            // IN (select * from xxx)
            else {
                switch (value.type().getBuilderType()) {
                    case BUILDER -> {
                        Builder b = Utils.castToType(value.value(), Builder.class);
                        assert b != null;
                        CompileResult r = b.compile();
                        s.append(r.getSqlStmt());
                        val = r.getParameter().getValues();
                    }
                    case STRING_ARRAY ->  {
                        var item = Utils.castToType(value.value(), List.class);
                        assert item != null;
                        for (int i = 0; i < item.size(); i++ ) {
                            s.append("?");
                            if (i < item.size() - 1) {
                                s.append(", ");
                            }
                            val.add(new StmtValue<String>(CustomType.ofType(JDBCType.VARCHAR), (String) item.get(i)));
                        }
                    }
                    case INTEGER_ARRAY -> {
                        var item = Utils.castToType(value.value(), List.class);
                        for (int i = 0; i < item.size(); i++ ) {
                            s.append("?");
                            if (i < item.size() - 1) {
                                s.append(", ");
                            }
                            val.add(new StmtValue<>(CustomType.ofType(JDBCType.VARCHAR), (Integer) item.get(i)));
                        }
                    }
                }
            }

            StringBuilder result = new StringBuilder();
            // TODO: change to compile()
            result.append(columnName.getStmt());

            if (operator.contains("IN")) {
                result.append(" ");
                result.append(" (");
                result.append(s);
                result.append(")");
            } else if (!operator.isBlank()) {
                result.append(operator);
                result.append(" ?");
            }

            return new CompileResult(result.toString(), new StmtParameter(val));
            // return new CompileResult(columnName.getStmt() + (operator.isBlank() ? "" : " " +operator+" ?"), new StmtParameter(value));
    }
}

class WhereStmt extends StmtCompile {
    protected List<WhereBlock> blocks;

    public WhereStmt() {
        blocks = new ArrayList<>();
    }

    public boolean isGroup() { return true; }
    public boolean isNotEmpty() { return !blocks.isEmpty(); }

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

/*
class JoinStmt extends StmtCompile {
    public String type;
    public String tableName;
    public String firstKey;
    public String operator;
    public String secondKey;

    JoinStmt(String type, String tableName, String firstKey, String operator, String secondKey) {
        this.type = type;
        this.tableName = tableName;
        this.firstKey = firstKey;
        this.operator = operator;
        this.secondKey = secondKey;
    }

    public JoinStmt on(String firstKey, String operator, String secondKey) {
        where(firstKey, operator, secondKey);
        return this;
    }

    @Override
    public CompileResult compile() {
        // var r = tableName.compile();

        // return new CompileResult(type + " JOIN " + r.getSqlStmt()
        //         + " ON " + firstKey + " " + operator  + " " + secondKey, r.getParameter());
        // if (tableName.isRaw()) {
        // } else {
        //     return new CompileResult(" " + type + " " + r.getSqlStmt()
        //             + " ON " + firstKey + " " + operator  + " " + secondKey, r.getParameter());
        // }
        return new CompileResult("");
    }
}
*/

public class Builder implements BlockStmt {

    private List<RawStr> selects;

    protected List<Table> from;

    private List<JoinClause> join;

    // private WhereGroup groupRoot;
    protected WhereStmt wheres;
//    private Connection connection;


    public Builder() {
        selects = new ArrayList<RawStr>();
        from = new ArrayList<>();

        // groupRoot = new WhereGroup();
        // wheres = groupRoot;
        wheres = new WhereStmt();

        join = new ArrayList<>();

    }

    public Builder from(String table) {
        return from(table, "");
    }

    public Builder from(String table, String as) {
        from.add(new Table(table, as));
        return this;
    }

    public Builder select(String... fields) {
        selects.addAll(Arrays.stream(fields).map(EscapeStr::new).toList());
        return this;
    }


    public Builder where(String column, String value) {
        wheres.add(new WhereCompare(EscapeStr.of(column), "=", new StmtValue<>(CustomType.ofType(JDBCType.VARCHAR), value)), true);
        return this;
    }

    public Builder where(String column, String operator, String value) {
        wheres.add(new WhereCompare(EscapeStr.of(column), operator, new StmtValue<>(CustomType.ofType(JDBCType.VARCHAR), value)), true);
        return this;
    }

    public Builder orWhere(String column, String value) {
        wheres.add(new WhereCompare(EscapeStr.of(column), "=", new StmtValue<>(CustomType.ofType(JDBCType.VARCHAR), value)), false);
        return this;
    }

    public Builder where(RawStr rawStmt, IntArray values) {

        wheres.add(
                new WhereCompare(rawStmt, "",
                        new StmtValue<>(CustomType.ofType(BuilderType.INTEGER_ARRAY), values)), true);

        return this;
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

            wheres.add(
                    new WhereCompare(EscapeStr.of(column), "IN",
                        new StmtValue<>(CustomType.ofType(BuilderType.STRING_ARRAY), values)), true);

        return this;
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
        StringBuilder s = new StringBuilder();
        List<StmtValue<?>>  values = new LinkedList<>();

        if (!selects.isEmpty()) {
            CompileResult r = Utils.stmtJoin(selects, ", ");
            s.append(" SELECT ").append(r.getSqlStmt());
        } else {
            s.append(" SELECT * ");
        }

        if (!from.isEmpty()) {
            CompileResult r = Utils.stmtJoin(from, ", ");
            s.append(" FROM ").append(r.getSqlStmt());
            values.addAll(r.getParameter().getValues());
        }

        if (!join.isEmpty()) {
            CompileResult r = Utils.stmtJoin(join, " ");
            s.append(" ").append(r.getSqlStmt());
            values.addAll(r.getParameter().getValues());
        }

        if (wheres.isNotEmpty()) {
            CompileResult r = wheres.compile();
            s.append(" WHERE ").append(r.getSqlStmt());
            values.addAll(r.getParameter().getValues());
        }

        return new CompileResult(s.toString(), new StmtParameter(values));
    }
}
