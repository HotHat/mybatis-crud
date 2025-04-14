package com.lyhux.mybatiscrud.builder.vendor;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignListExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueGroupExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.GroupByExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;

import java.util.ArrayList;
import java.util.List;

public abstract class Grammar {
    protected String tablePrefix = "";

    public abstract String getEscapeChars();

    public Grammar setTablePrefix(String prefix) {
        tablePrefix = prefix;
        return this;
    }

    public String escapeField(String field, boolean isTableName) {
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

                sb.append(escapeChar);
                if (isTableName || (innerCount == 0 && dotSplit.length == 2)) {
                    sb.append(tablePrefix);
                }
                sb.append(d);
                sb.append(escapeChar);
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
                    // compileExpr(sb, bindings, condition);
                }

                if (showBraces) {
                    sb.append(")");
                }
                return new ExprResult(sb.toString(), bindings);
            }
            case BinaryExpr b -> {
                // column
                String column = compile(b.getColumn());
                sb.append(column);
                // operator
                if (!column.isBlank() && !b.getOperator().isBlank()) {
                    sb.append(" ");
                }
                sb.append(b.getOperator());

                if (b.isShowBrace()) {
                    sb.append(" ").append("(");
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
                    compileExpr(sb, bindings, compile(value.stmt()));
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
                return this.escapeField(es.getValue(), es.isTableName());
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

        // sb.append("");
        var bindings = new ArrayList<TypeValue<?>>();
        if (refs.size() > 1) {
            sb.append("(");
        }

        int count = 0;
        for (var exp : refs) {
            compileExpr(sb, bindings, compile(exp));
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
            sb.append(" ");
            compileExpr(sb, bindings, compile(exp));
        }


        return new ExprResult(sb.toString(), bindings);
    }


    public ExprResult compile(TableFactorExpr expr) {
        switch (expr) {
            case TableNameExpr t -> {
                return compile(t);
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

    public ExprResult compile(TableNameExpr expr) {
        var sb = new StringBuilder();
        sb.append(compile(expr.getTableName()));
        if (!expr.getAlias().getValue().isBlank()) {
            sb.append(" AS ")
              .append(compile(expr.getAlias()));
        }
        return new ExprResult(sb.toString(), List.of());
    }

    public ExprResult compile(TableJoinedExpr expr) {
        var sb = new StringBuilder();

        var joined = expr.getJoined();
        var factor = expr.getTableFactor();
        var condition = expr.getCondition();

        sb.append(joined).append(" JOIN ");

        var result = compile(factor);
        sb.append(result.statement());
        var bindings = new ArrayList<>(result.bindings());

        sb.append(" ON ");

        compileExpr(sb, bindings, compile(condition));


        return new ExprResult(sb.toString(), bindings);
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
        // ExprResult result;
        var bindings = new ArrayList<TypeValue<?>>();

        // select
        sb.append("SELECT ");
        compileExpr(sb, bindings, compile(selectExpr));

        // from
        if (!tableRefs.isEmpty()) {
            sb.append(" FROM ");
            compileExpr(sb, bindings, compile(tableRefs));
        }

        // where
        if (!whereExpr.isEmpty()) {
            sb.append(" WHERE ");
            compileExpr(sb, bindings, compile(whereExpr));
        }

        // group by having
        if (!groupByExpr.isEmpty()) {
            sb.append(" ");
            compileExpr(sb, bindings, compile(groupByExpr));
        }

        // order by
        if (!orderByExpr.isEmpty()) {
            sb.append(" ").append(compile(orderByExpr).statement());
        }

        // union bracket
        if (!unionClause.getUnionItems().isEmpty()) {
            sb.append(")");
        }

        // union clause
        for (var union : unionClause.getUnionItems()) {
            sb.append(" ").append(union.type()).append(" ");

            sb.append("(");
            compileExpr(sb, bindings, compile(union.selectStmt()));
            sb.append(")");
        }

        // limit
        if (limitExpr != null) {
            sb.append(" ").append(compile(limitExpr).statement());
        }

        // for
        if (forExpr != null) {
            sb.append(" ").append(compile(forExpr).statement());
        }


        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(GroupByExpr expr) {
        var sb = new StringBuilder();
        var columns = expr.getColumns();
        var having = expr.getHaving();

        sb.append("GROUP BY ");
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
            compileExpr(sb, bindings, compile(having.getExpr()));
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(OrderByExpr expr) {
        var sb = new StringBuilder();
        var columns = expr.getItems();

        if (!columns.isEmpty()) {
            sb.append("ORDER BY ");
            int count = 0;
            for (var exp : columns) {
                sb.append(compile(exp.column())).append(" ").append(exp.order().toUpperCase());
                if (++count < columns.size()) { sb.append(", "); }
            }
        }

        return new ExprResult(sb.toString(), List.of());
    }

    public ExprResult compile(LimitExpr expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("LIMIT ").append(expr.rowCount());
        if (expr.offset() > 0) {
            sb.append(" OFFSET ").append(expr.offset());
        }

        return new ExprResult(sb.toString(), List.of());
    }

    public ExprResult compile(ForExpr expr) {
        var sb = new StringBuilder();
        var tableNames = expr.getTableNames();
        var mode = expr.getModel();

        sb.append("FOR ");
        sb.append(mode.toUpperCase());
        if (!tableNames.isEmpty()) {
            sb.append(" OF ");
            int count = 0;
            for (var exp : tableNames) {
                sb.append(compile(exp));
                if (++count < tableNames.size()) { sb.append(", "); }
            }
        }

        return new ExprResult(sb.toString(), List.of());
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

    public ExprResult compile(DuplicateAssignExpr expr) {

        var column = compile(expr.column());
        var result = compile(expr.value().expr());
        var bindings = new ArrayList<TypeValue<?>>(expr.value().bindings());
        return new ExprResult(column + '=' + result, bindings);
    }

    public ExprResult compile(DuplicateAssignListExpr expr) {
        var sb = new StringBuilder();
        var assigns = expr.getAssignExpr();
        var bindings = new ArrayList<TypeValue<?>>();
        ExprResult result;

        int count = 0;
        for (var exp : assigns) {
            compileExpr(sb, bindings, compile(exp));
            if (++count < assigns.size()) { sb.append(", "); }
        }

        return new ExprResult(sb.toString(), bindings);
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
                compileExpr(sb, bindings, compile(value.stmt()));
            }

            if (++count < assigns.size()) { sb.append(", "); }
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(InsertStmt stmt) {
        var sb = new StringBuilder();
        ExprResult result;

        var tableRef = stmt.tableRef();
        var columns = stmt.columns();
        var values = stmt.values();
        var assigns = stmt.assigns();

        sb.append("INSERT INTO ");
        result = compile(tableRef);
        sb.append(result.statement());
        var bindings = new ArrayList<TypeValue<?>>(result.bindings());
        //
        if (!columns.isEmpty()) {
            result = compile(columns);
            sb.append(" (");
            sb.append(result.statement());
            sb.append(")");
            bindings.addAll(result.bindings());
        }
        //
        sb.append(" VALUES ");
        var valueGroup = values.getValues();
        int count = 0;
        for (var val : valueGroup) {
            sb.append("(");
            compileExpr(sb, bindings, compile(val));
            sb.append(")");

            if (++count < valueGroup.size()) { sb.append(", "); }
        }

        if (!assigns.isEmpty()) {
            sb.append(" ON DUPLICATE KEY UPDATE ");
            compileExpr(sb, bindings, compile(assigns));
        }

        return new ExprResult(sb.toString(), bindings);
    }

    public ExprResult compile(Expr expr) {
        return switch (expr) {
            case WhereClauseExpr w -> compile(w);
            case ColumnExpr c -> compile(c);
            case TableRefsExpr trs -> compile(trs);
            case TableRefExpr tr -> compile(tr);
            // before TableFactorExpr
            case TableSubExpr ts -> compile(ts);
            case TableNameExpr tn -> compile(tn);
            case TableFactorExpr tf -> compile(tf);
            case TableJoinedExpr tj -> compile(tj);
            case GroupByExpr gb -> compile(gb);
            case OrderByExpr ob -> compile(ob);
            case LimitExpr lm -> compile(lm);
            case ForExpr fr -> compile(fr);
            case ValueGroupExpr vg -> compile(vg);
            case DuplicateAssignListExpr dal -> compile(dal);
            case DuplicateAssignExpr da -> compile(da);
            case AssignListExpr al -> compile(al);

            default -> throw new IllegalStateException("Unexpected value: " + expr);
        };
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
            compileExpr(sb, bindings, compile(assigns));
        }

        // where
        if (!whereExpr.isEmpty()) {
            sb.append(" WHERE ");
            compileExpr(sb, bindings, compile(whereExpr));
        }

        if (!orderBy.isEmpty()) {
            sb.append(" ");
            sb.append(compile(orderBy).statement());
        }

        if (limit != null) {
            sb.append(" ");
            sb.append(compile(limit).statement());
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
            compileExpr(sb, bindings, compile(whereExpr));
        }

        if (!orderBy.isEmpty()) {
            sb.append(" ");
            sb.append(compile(orderBy).statement());
        }

        if (limit != null) {
            sb.append(" ");
            sb.append(compile(limit).statement());
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

    private void compileExpr(StringBuilder sb, List<TypeValue<?>> bindings, ExprResult result) {
       sb.append(result.statement());
       bindings.addAll(result.bindings());
    }

}
