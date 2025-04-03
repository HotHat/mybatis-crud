package com.lyhux.mybatiscrud.builder.vendor;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignListExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueGroupExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.GroupByExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;

import java.util.ArrayList;

public abstract class Grammar {
    public abstract String getEscapeChars();

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
            var escapeChar = getEscapeChars();

            int innerCount = 0;
            for (String d : dotSplit) {
                //
                if (d.equals("*")) {
                    sb.append(d);
                    innerCount++;
                    continue;
                }

                sb.append(escapeChar).append(d).append(escapeChar);
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
        var sb = new StringBuilder();
        var bindings = new ArrayList<TypeValue<?>>();

        switch (expr)
        {
            case WhereExpr w -> {
                // top level
                var conditions = w.getConditions();

                boolean showBraces = w.isShowBraces();

                // in group include inverse logic connect
                // boolean includeInverse = true;
                // String currentBool = w.getBool();
                // for (var condition : conditions) {
                //     var bool = switch (condition) {
                //         case WhereExpr w1 -> w1.getBool();
                //         case BinaryExpr b1 -> currentBool;
                //     };
                //     if (!bool.equals(currentBool)) { includeInverse = true; break; }
                // }

                if (showBraces) {
                    sb.append("(");
                }

                int count = 0;
                for (var condition : conditions) {
                    var bool = switch (condition) {
                        case WhereExpr w1 -> w1.getBool();
                        case BinaryExpr b1 -> "";
                    };

                    if (count > 0) {
                        sb.append(" ").append(bool).append(" ");
                    }
                    count++;

                    var result = compile(condition);
                    sb.append(result.statement());
                    bindings.addAll(result.bindings());
                }

                if (showBraces) {
                    sb.append(")");
                }
                return new ExprResult(sb.toString(), bindings);
            }
            case BinaryExpr b -> {
                // column
                sb.append(compile(b.getColumn()));
                // operator
                sb.append(" ").append(b.getOperator());

                if (b.isShowBrace()) {
                    sb.append("(");
                }
                // value
                var value = b.getValue();
                // is expr
                if (value.isExpr()) {
                    var statement = compile(value.expr());
                    if (!statement.isBlank()) {
                        sb.append(" ").append(statement);
                    }

                    if (!value.bindings().isEmpty()) {
                        bindings.addAll(value.bindings());
                    }
                }
                // is select stmt
                else {
                    var result = compile(value.stmt());
                    sb.append(result.statement());
                    bindings.addAll(result.bindings());
                }

                if (b.isShowBrace()) {
                    sb.append(")");
                }

                return new ExprResult(sb.toString(), bindings);
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

    public abstract String escapeRawStr(ExprStr expr);


    public ExprResult compile(ColumnExpr expr) {
        var sb = new StringBuilder();
        int count = 0;
        var selects = expr.getSelect();
        if (selects.isEmpty()) {
            sb.append("*");
        }
        for (var exp : selects) {
            switch (exp) {
                case RawStr s -> { sb.append(s.getValue()); }
                case EscapedStr es -> { sb.append(compile(es)); }
            }

            if (++count < selects.size()) {
                sb.append(", ");
            }
        }

        return new ExprResult(sb.toString(), expr.getBindings());
    }

    public ExprResult compile(TableRefsExpr expr) {
        var sb = new StringBuilder();

        var refs = expr.getTableRefs();

        var bindings = new ArrayList<TypeValue<?>>();
        if (refs.size() > 1) {
            sb.append("(");
        }

        int count = 0;
        for (var exp : refs) {
            var result = compile(exp);
            sb.append(result.statement());
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
        sb.append(result.statement());

        var bindings = new ArrayList<>(result.bindings());

        for (var exp : joined) {
            result = compile(exp);
            sb.append(result.statement());
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

        var subTable = expr.selectStmt();
        var alias = expr.alias();

        var result = compile(subTable);
        sb.append("(").append(result.statement()).append(")");
        var r = new ArrayList<TypeValue<?>>(result.bindings());

        if (!alias.isBlank()) {
            sb.append(" AS ").append(alias);
        }

        return new ExprResult(sb.toString(), r);
    }

    public String compile(TableNameExpr expr) {
        var sb = new StringBuilder();
        sb.append(compile(expr.getTableName()));
        if (!expr.getAlias().getValue().isBlank()) {
            sb.append(" AS ")
              .append(compile(expr.getAlias()));
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
        sb.append(result.statement());
        var r = new ArrayList<>(result.bindings());

        sb.append(" ON ");

        result = compile(condition);
        sb.append(result.statement());
        r.addAll(result.bindings());

        return new ExprResult(sb.toString(), r);
    }

    public ExprResult compile(SelectStmt stmt) {
        var sb = new StringBuilder();

        var selectExpr = stmt.select();
        var tableRefs = stmt.tableRefs();
        var whereExpr = stmt.where();
        var groupByExpr = stmt.groupBy();
        var orderByExpr = stmt.orderBy();
        var limitExpr = stmt.limit();
        var unionClause = stmt.union();
        var forExpr = stmt.forUpdate();

        // union bracket
        if (!unionClause.getUnionItems().isEmpty()) {
            sb.append("(");
        }

        //
        ExprResult result;

        // select
        result = compile(selectExpr);
        sb.append("SELECT ")
          .append(result.statement());

        var bindings = new ArrayList<TypeValue<?>>(result.bindings());


        // from
        if (!tableRefs.isEmpty()) {
            sb.append(" FROM ");
            result = compile(tableRefs);
            sb.append(result.statement());
            bindings.addAll(result.bindings());
        }

        // where
        if (!whereExpr.isEmpty()) {
            sb.append(" WHERE ");
            result = compile(whereExpr);
            sb.append(result.statement());
            bindings.addAll(result.bindings());
        }

        // group by having
        if (!groupByExpr.isEmpty()) {
            sb.append(" GROUP BY ");
            result = compile(groupByExpr);
            sb.append(result.statement());
            bindings.addAll(result.bindings());
        }

        // order by
        if (!orderByExpr.isEmpty()) {
            sb.append(" ORDER BY ").append(compile(orderByExpr));
        }

        // union bracket
        if (!unionClause.getUnionItems().isEmpty()) {
            sb.append(")");
        }

        // union clause
        for (var union : unionClause.getUnionItems()) {
            sb.append(" ").append(union.type()).append(" ");

            sb.append("(");
            result = compile(union.selectStmt());
            sb.append(result.statement());
            bindings.addAll(result.bindings());
            sb.append(")");
        }

        // limit
        if (limitExpr != null) {
            sb.append(" LIMIT ").append(compile(limitExpr));
        }

        // for
        if (forExpr != null) {
            sb.append(" FOR ").append(compile(forExpr));
        }


        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(GroupByExpr expr) {
        var sb = new StringBuilder();
        var columns = expr.getColumns();
        var having = expr.getHaving();

        if (!columns.isEmpty()) {
            int count = 0;
            for (var exp : columns) {
                sb.append(compile(exp));
                if (++count < columns.size()) { sb.append(", "); }
            }
        }

        var bindings = new ArrayList<TypeValue<?>>();
        if (!having.isEmpty()) {
            sb.append(" HAVING ");
            var result = compile(having.getExpr());
            sb.append(result.statement());
            bindings.addAll(result.bindings());
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public String compile(OrderByExpr expr) {
        var sb = new StringBuilder();
        var columns = expr.getItems();

        if (!columns.isEmpty()) {
            int count = 0;
            for (var exp : columns) {
                sb.append(compile(exp.column())).append(" ").append(exp.order().toUpperCase());
                if (++count < columns.size()) { sb.append(", "); }
            }
        }

        return sb.toString();
    }

    public String compile(LimitExpr expr) {
        return expr.rowCount() + (expr.offset() > 0 ? " OFFSET " + expr.offset() : "");
    }

    public String compile(ForExpr expr) {
        var sb = new StringBuilder();
        var tableNames = expr.getTableNames();
        var mode = expr.getModel();

        sb.append(mode.toUpperCase());
        if (!tableNames.isEmpty()) {
            sb.append(" OF ");
            int count = 0;
            for (var exp : tableNames) {
                sb.append(compile(exp));
                if (++count < tableNames.size()) { sb.append(", "); }
            }
        }

        return sb.toString();
    }

    public ExprResult compile(ValueGroupExpr expr) {
        var sb = new StringBuilder();
        var bindings = new ArrayList<TypeValue<?>>();
        var columns = expr.getValueExpr();
        int count = 0;
        for (var exp : columns) {
            var column = exp.expr();
            sb.append(escapeRawStr(column));
            if (exp.bindings() != null) {
                bindings.addAll(exp.bindings());
            }

            if (++count < columns.size()) { sb.append(", "); }
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public String compile(DuplicateAssignExpr expr) {
        return compile(expr.column()) + '=' + compile(expr.value());
    }

    public String compile(DuplicateAssignListExpr expr) {
        var sb = new StringBuilder();
        var assigns = expr.getAssignExpr();
        int count = 0;
        for (var exp : assigns) {
            sb.append(compile(exp));
            if (++count < assigns.size()) { sb.append(", "); }
        }

        return sb.toString();
    }

    public ExprResult compile(AssignListExpr expr) {
        var sb = new StringBuilder();
        var bindings = new ArrayList<TypeValue<?>>();

        var assigns = expr.getAssignExpr();
        int count = 0;
        for (var exp : assigns) {
            sb.append(compile(exp.column()));
            sb.append("=");

            var value = exp.value();
            if (value.isExpr()) {
                sb.append(escapeRawStr(value.expr()));
                bindings.addAll(value.bindings());
            } else  {
                var result = compile(value.stmt());
                sb.append(result.statement());
                bindings.addAll(result.bindings());
            }

            if (++count < assigns.size()) { sb.append(", "); }
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(InsertStmt stmt) {
        var sb = new StringBuilder();
        var bindings = new ArrayList<TypeValue<?>>();

        var tableRef = stmt.tableRef();
        var columns = stmt.columns();
        var values = stmt.values();
        var assigns = stmt.assigns();

        sb.append("INSERT INTO ");
        sb.append(compile(tableRef));
        //
        if (!columns.isEmpty()) {
            sb.append(" (");
            sb.append(compile(columns));
            sb.append(" )");
        }
        //
        sb.append(" VALUES ");
        var valueGroup = values.getValues();
        int count = 0;
        for (var val : valueGroup) {
            sb.append("(");
            var result = compile(val);
            sb.append(result.statement());
            bindings.addAll(result.bindings());
            sb.append(")");

            if (++count < valueGroup.size()) { sb.append(", "); }
        }

        if (!assigns.isEmpty()) {
            sb.append(" ON DUPLICATE KEY UPDATE ");
            sb.append(compile(assigns));
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(UpdateStmt stmt) {
        var sb = new StringBuilder();

        var tableRef = stmt.table();
        var assigns = stmt.assignments();
        var whereExpr = stmt.where();
        var orderBy = stmt.orderBy();
        var limit = stmt.limit();

        sb.append("UPDATE ");
        var result = compile(tableRef);
        sb.append(result.statement());
        var bindings = new ArrayList<TypeValue<?>>(result.bindings());

        // assignment
        if (!assigns.isEmpty()) {
            sb.append(" SET ");
            result = compile(assigns);
            sb.append(result.statement());
            bindings.addAll(result.bindings());
        }

        // where
        if (!whereExpr.isEmpty()) {
            sb.append(" WHERE ");
            result = compile(whereExpr);
            sb.append(result.statement());
            bindings.addAll(result.bindings());
        }

        if (!orderBy.isEmpty()) {
            sb.append(" ORDER BY ");
            sb.append(compile(orderBy));
        }

        if (limit != null) {
            sb.append(" LIMIT ");
            sb.append(compile(limit));
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(DeleteStmt stmt) {
        var sb = new StringBuilder();

        var tableRef = stmt.table();
        var whereExpr = stmt.where();
        var orderBy = stmt.orderBy();
        var limit = stmt.limit();

        sb.append("DELETE FROM ");
        var result = compile(tableRef);
        sb.append(result.statement());
        var bindings = new ArrayList<TypeValue<?>>(result.bindings());

        // where
        if (!whereExpr.isEmpty()) {
            sb.append(" WHERE ");
            result = compile(whereExpr);
            sb.append(result.statement());
            bindings.addAll(result.bindings());
        }

        if (!orderBy.isEmpty()) {
            sb.append(" ORDER BY ");
            sb.append(compile(orderBy));
        }

        if (limit != null) {
            sb.append(" LIMIT ");
            sb.append(compile(limit));
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(Stmt stmt) {
        return switch (stmt) {
            case SelectStmt select -> compile(select);
            case InsertStmt insert -> compile(insert);
            case UpdateStmt update -> compile(update);
            case DeleteStmt delete -> compile(delete);
        };
    }
}
