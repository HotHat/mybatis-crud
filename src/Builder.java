import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Array;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



class Table implements SqlClauseCompiler {
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
    public String compile() {
        if (asName.isEmpty()) {
            return tableName;
        } else {
            return tableName + " AS " + asName;
        }
    }
}

class WhereValue {
    public JDBCType type;
    public Object value;
    public WhereValue(JDBCType type, Object value) {
        this.type = type;
        this.value = value;
    }
}

/**
 * (a b ((c d) e (f (g h) i)) j k)
 */
class WhereBlock implements SqlClauseCompiler {
    public WhereJoin join;
    public WhereGroup group;

    private boolean hiddenJoin = false;


    public void setHiddenJoin(boolean hiddenJoin) {
        this.hiddenJoin = hiddenJoin;
    }

    public WhereBlock(WhereJoin join, WhereGroup group) {
        this.join = join;
        this.group = group;
    }

    @Override
    public String compile() {
        if (hiddenJoin) {
            if (group.isGroup()) {
                return " (" + group.compile() + ")";
            } else {
                return group.compile();
            }
        } else {
            if (group.isGroup()) {
                return " " + join.getName() + " (" + group.compile() + ")";
            } else {
                return " " + join.getName() + " " + group.compile();
            }
        }
    }
}

class WhereCompare extends WhereGroup implements SqlClauseCompiler {
    public String columnName;
    public String operator;
    public WhereValue value;

    public boolean isGroup() { return false; }

    WhereCompare(String columnName, String operator, WhereValue value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String compile() {
        return columnName + operator + value;
    }
}

//class WhereClause implements SqlClauseCompiler {
//    protected List<WhereCompare> wheres;
//
//    WhereClause() {
//        this.wheres = new ArrayList<>();
//    }
//
//    public void add(WhereCompare wc) {
//        wheres.add(wc);
//    }
//    public int size() { return wheres.size(); }
//
//    @Override
//    public String compile() {
//        return "";
//    }
//}
//
//class AndWhere extends WhereClause implements SqlClauseCompiler {
//
//    @Override
//    public String compile() {
//        return Utils.compileJoin(wheres, " AND ");
//    }
//}
//
//class OrWhere extends WhereClause implements SqlClauseCompiler {
//    @Override
//    public String compile() {
//        return Utils.compileJoin(wheres, " OR ");
//    }
//}

class WhereGroup implements SqlClauseCompiler {
    protected List<WhereBlock> blocks;

    public WhereGroup() {
        blocks = new ArrayList<>();
    }

    public boolean isGroup() { return true; }

    public void add(WhereGroup group, boolean isAnd) {
        blocks.add(new WhereBlock(isAnd ? WhereJoin.AND : WhereJoin.OR, group));
    }

    @Override
    public String compile() {
        var s = new StringBuilder();

        for (int i = 0; i < blocks.size(); i++) {
            var current = blocks.get(i);
            if (i == 0) {
                current.setHiddenJoin(true);
            }

            s.append(current.compile());
        }

        return s.toString();
    }
}


public class Builder {

    private List<String> selects;

    private List<Table> from;

    private WhereGroup groupRoot;
    private WhereGroup wheres;
//    private Connection connection;


    public Builder() {
        selects = new ArrayList<String>();
        from = new ArrayList<>();

        groupRoot = new WhereGroup();
        wheres = groupRoot;
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
        wheres.add(new WhereCompare(column, "=", new WhereValue(JDBCType.VARCHAR, value)), true);
        return this;
    }
    public Builder orWhere(String column, String value) {
        wheres.add(new WhereCompare(column, "=", new WhereValue(JDBCType.VARCHAR, value)), false);
        return this;
    }

    public Builder where(WhereQuery query) {
        var preWhere = wheres;
        wheres = new WhereGroup();
        preWhere.add(wheres, true);

        query.where(this);
        wheres = preWhere;
        return this;
    }

    public <T extends JDBCType> Builder whereIn(String column, List<T> values) throws SQLException {
        Type genericSuperclass = values.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
//            System.out.println("Generic type: " + typeArguments[0].getTypeName()); // Output: String
            String typeName = typeArguments[0].getTypeName();
//            Array arr = connection.createArrayOf(typeName , values.toArray());

            wheres.add(new WhereCompare(column, " IN ", new WhereValue(JDBCType.valueOf(typeName), values)), true);
        } else {
            throw new SQLException("Type mismatch");
        }
        return this;
    }

    @Override
    public String toString() {
        String s = "select (" +
                Utils.strJoin(selects, ", ") +
                ")" +
                " from " + Utils.compileJoin(from, ", ") +
                " where " + groupRoot.compile()
                ;

        return s;
    }

}
