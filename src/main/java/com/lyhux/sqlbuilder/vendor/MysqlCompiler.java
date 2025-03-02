package com.lyhux.sqlbuilder.vendor;

import com.lyhux.sqlbuilder.grammar.*;

import java.util.ArrayList;

public class MysqlCompiler {
    public String escapeField(String field) {
        var sb = new StringBuilder();
        String[] asSplit = field.split("\\s+(as|AS)\\s+");

        int outCount = 0;
        for (String s : asSplit) {
            //
            if (s.equals("*")) {
                sb.append(s);
                outCount++;
                continue;
            }

            var dotSplit = s.split("\\.");

            int innerCount = 0;
            for (String d : dotSplit) {
                //
                if (d.equals("*")) {
                    sb.append(d);
                    innerCount++;
                    continue;
                }

                sb.append('`').append(d).append('`');
                if (++innerCount < dotSplit.length) {
                    sb.append(".");
                }
            }

            if (++outCount < asSplit.length) {
                sb.append(" AS ");
            }
        }

        return sb.toString();
    }

    public ExprResult compile(WhereClauseExpr expr) {
        var s = new StringBuilder();
        var r = new ArrayList<ExprValue<?>>();

        switch (expr)
        {
            case WhereExpr w -> {
                // top level
                var conditions = w.getConditions();

                int count = 0;
                if (conditions.size() > 1 && !w.isRoot()) {
                    s.append("(");
                }
                for (var condition : conditions) {
                    var bool = switch (condition) {
                        case WhereExpr w1 -> w1.getBool();
                        case BinaryExpr b1 -> "";
                    };

                    if (count > 0) {
                        s.append(" ").append(bool).append(" ");
                    }
                    count++;

                    var result = compile(condition);
                    s.append(result.sql());
                    r.addAll(result.bindings());
                }

                if (conditions.size() > 1 && !w.isRoot()) {
                    s.append(")");
                }
                return new ExprResult(s.toString(), r);
            }
            case BinaryExpr b -> {
                // column
                s.append(compile(b.getColumn()));
                // operator
                s.append(" ").append(b.getOperator()).append(" ");
                // value
                s.append(compile(b.getValue()));
                r.addAll(b.getBindings());

                return new ExprResult(s.toString(), r);
            }
        }
    }

    public String compile(ExprStr expr) {
        switch (expr)
        {
            case RawStr s -> {
                return s.getValue();
            }
            case EscapedStr es -> {
                return this.escapeField(es.getValue());
            }
        }
    }

    public String compile(SelectExpr expr) {
        var sb = new StringBuilder();
        int count = 0;
        var selects = expr.getSelect();
        for (var exp : selects) {
            switch (exp) {
                case RawStr s -> { sb.append(s.getValue()); }
                case EscapedStr es -> { sb.append(compile(es)); }
            }

            if (++count < selects.size()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public ExprResult compile(TableRefsExpr expr) {
        var sb = new StringBuilder();

        var refs = expr.getTableRefs();

        var bindings = new ArrayList<ExprValue<?>>();
        if (refs.size() > 1) {
            sb.append("(");
        }

        int count = 0;
        for (var exp : refs) {
            var result = compile(exp);
            sb.append(result.sql());
            bindings.addAll(result.bindings());
            if (++count < refs.size()) {
                sb.append(", ");
            }
        }

        if (refs.size() > 1) {
            sb.append(")");
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(TableRefExpr expr) {
        var sb = new StringBuilder();

        var factor = expr.getTableFactor();
        var joined = expr.getJoined();

        var result = compile(factor);
        sb.append(result.sql());

        var bindings = new ArrayList<>(result.bindings());

        for (var exp : joined) {
            result = compile(exp);
            sb.append(result.sql());
            bindings.addAll(result.bindings());
        }


        return new ExprResult(sb.toString(), bindings);
    }


    public ExprResult compile(TableFactorExpr expr) {
        switch (expr) {
            case TableNameExpr t -> {
                return new ExprResult(compile(t), new ArrayList<>());
            }
            case TableSubExpr tf -> {
                return compile(tf);
            }
        }
    }

    public ExprResult compile(TableSubExpr expr) {
        var sb = new StringBuilder();

        var subTable = expr.getSelectStmt();
        var alias = expr.getAlias();

        var result = compile(subTable);
        sb.append("(").append(result.sql()).append(")");
        var r = new ArrayList<ExprValue<?>>(result.bindings());

        if (!alias.isBlank()) {
            sb.append(" AS ").append(alias);
        }

        return new ExprResult(sb.toString(), r);
    }

    public String compile(TableNameExpr expr) {
        var sb = new StringBuilder();
        sb.append(expr.getTableName());
        if (!expr.getAlias().isBlank()) {
            sb.append(" AS ").append(expr.getAlias());
        }
        return sb.toString();
    }

    public ExprResult compile(TableJoinedExpr expr) {
        var sb = new StringBuilder();

        var joined = expr.getJoined();
        var factor = expr.getTableFactor();
        var condition = expr.getCondition();

        sb.append(" ").append(joined).append(" JOIN ");

        var result = compile(factor);
        sb.append(result.sql());
        var r = new ArrayList<>(result.bindings());

        sb.append(" ON ");

        result = compile(condition);
        sb.append(result.sql());
        r.addAll(result.bindings());

        return new ExprResult(sb.toString(), r);
    }

    public ExprResult compile(SelectStmt stmt) {
        var sb = new StringBuilder();

        var selectExpr = stmt.getSelectExpr();
        var tableRefs = stmt.getTableRefsExpr();
        var whereExpr = stmt.getWhereExpr();

        sb.append("SELECT ")
                .append(compile(selectExpr));

        ExprResult result;
        var bindings = new ArrayList<ExprValue<?>>();

        if (!tableRefs.isEmpty()) {
            sb.append(" FROM ");
            result = compile(tableRefs);
            sb.append(result.sql());
            bindings.addAll(result.bindings());
        }

        if (!whereExpr.isEmpty()) {
            sb.append(" WHERE ");
            result = compile(whereExpr);
            sb.append(result.sql());
            bindings.addAll(result.bindings());
        }

        return new ExprResult(sb.toString(), bindings);
    }
}
