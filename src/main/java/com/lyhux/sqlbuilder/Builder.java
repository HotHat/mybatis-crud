package main.java.com.lyhux.sqlbuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



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
        List<StmtValue> values = new ArrayList<>(r.getParameter());

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
    public String columnName;
    public String operator;
    public StmtValue value;

    public boolean isGroup() { return false; }

    WhereCompare(String columnName, String operator, StmtValue value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public CompileResult compile() {
        return new CompileResult(columnName + operator + "?", Arrays.asList(value));
    }
}

class WhereStmt extends StmtCompile {
    protected List<WhereBlock> blocks;

    public WhereStmt() {
        blocks = new ArrayList<>();
    }

    public boolean isGroup() { return true; }
    public boolean isEmpty() { return blocks.isEmpty(); }

    public void add(WhereStmt group, boolean isAnd) {
        blocks.add(new WhereBlock(isAnd ? WhereJoin.AND : WhereJoin.OR, group));
    }

    @Override
    public CompileResult compile() {
        var s = new StringBuilder();
        List<StmtValue> values = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            var current = blocks.get(i);
            if (i == 0) {
                current.setHiddenJoin(true);
            }
            CompileResult r = current.compile();
            s.append(r.getSqlStmt());
            values.addAll(r.getParameter());
        }

        return new CompileResult(s.toString(), values);
    }
}


class JoinStmt extends StmtCompile {
    public BlockStmt tableName;
    public String firstKey;
    public String operator;
    public String secondKey;

    JoinStmt(BlockStmt tableName, String firstKey, String operator, String secondKey) {
        this.tableName = tableName;
        this.firstKey = firstKey;
        this.operator = operator;
        this.secondKey = secondKey;
    }

    @Override
    public CompileResult compile() {
        return new CompileResult(tableName + " ON " + firstKey + " " + operator  + " " + secondKey);
    }
}

public class Builder implements BlockStmt {

    private List<String> selects;

    private List<Table> from;

    private List<JoinStmt> join;

    // private WhereGroup groupRoot;
    private WhereStmt wheres;
//    private Connection connection;


    public Builder() {
        selects = new ArrayList<String>();
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
        selects.addAll(Arrays.asList(fields));
        return this;
    }


    public Builder where(String column, String value) {
        wheres.add(new WhereCompare(column, "=", new StmtValue(JDBCType.VARCHAR, value)), true);
        return this;
    }
    public Builder orWhere(String column, String value) {
        wheres.add(new WhereCompare(column, "=", new StmtValue(JDBCType.VARCHAR, value)), false);
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

    public <T extends String> Builder whereIn(String column, List<? extends T> values) throws SQLException {
        Type genericSuperclass = values.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
//            System.out.println("Generic type: " + typeArguments[0].getTypeName()); // Output: String
            String typeName = typeArguments[0].getTypeName();
//            Array arr = connection.createArrayOf(typeName , values.toArray());
            String type = typeName.equals("CharSequence") ? "VARCHAR" : "BIGINT";

            wheres.add(new WhereCompare(column, " IN ", new StmtValue(JDBCType.valueOf(type), values)), true);
        } else {
            throw new SQLException("Type mismatch");
        }
        return this;
    }


    public Builder join(String table, String leftKey, String op, String rightKey) {
        join.add(new JoinStmt(new RawStmt(table), leftKey, op, rightKey));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        if (!selects.isEmpty()) {
            s.append(" SELECT ")
                    .append(Utils.strJoin(selects, ", "));
        } else {
            s.append(" SELECT * ");
        }

        if (!from.isEmpty()) {
            s.append(" FROM ").append(Utils.compileJoin(from, ", "));
        }

        if (!join.isEmpty()) {
            s.append(" JOIN ").append(Utils.compileJoin(join, ", "));
        }

        if (!wheres.isEmpty()) {
            s.append(" WHERE ").append(wheres.compile());
        }

        return s.toString();
    }

    @Override
    public boolean isBuilder() {
        return true;
    }

    @Override
    public CompileResult compile() {
        StringBuilder s = new StringBuilder();
        List<StmtValue> values = new ArrayList<>();

        if (!selects.isEmpty()) {
            s.append(" SELECT ")
                    .append(Utils.strJoin(selects, ", "));
        } else {
            s.append(" SELECT * ");
        }

        if (!from.isEmpty()) {
            CompileResult r = Utils.stmtJoin(from, ", ");
            s.append(" FROM ").append(r.getSqlStmt());
            values.addAll(r.getParameter());
        }

        if (!join.isEmpty()) {
            CompileResult r = Utils.stmtJoin(join, ", ");
            s.append(" JOIN ").append(r.getSqlStmt());
            values.addAll(r.getParameter());
        }

        if (!wheres.isEmpty()) {
            CompileResult r = wheres.compile();
            s.append(" WHERE ").append(r.getSqlStmt());
            values.addAll(r.getParameter());
        }

        return new CompileResult(s.toString(), values);
    }
}
