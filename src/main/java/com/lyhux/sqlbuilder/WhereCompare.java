package main.java.com.lyhux.sqlbuilder;

import java.sql.JDBCType;
import java.util.LinkedList;
import java.util.List;

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
                case RAW_STRING -> {
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

            if (!value.type().isJDBCType()  && value.type().getBuilderType() == BuilderType.RAW_STRING) {
                result.append((String) value.value());
            } else {
                result.append(" ?");
            }
        }

        return new CompileResult(result.toString(), new StmtParameter(val));
        // return new CompileResult(columnName.getStmt() + (operator.isBlank() ? "" : " " +operator+" ?"), new StmtParameter(value));
    }
}