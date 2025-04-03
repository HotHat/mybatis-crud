package com.lyhux.mybatiscrud.builder.vendor;

import com.lyhux.mybatiscrud.builder.grammar.*;
import com.lyhux.mybatiscrud.builder.grammar.select.ForExpr;
import com.lyhux.mybatiscrud.builder.grammar.select.GroupByExpr;
import com.lyhux.mybatiscrud.builder.grammar.ColumnExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.DuplicateAssignListExpr;
import com.lyhux.mybatiscrud.builder.grammar.insert.ValueGroupExpr;
import com.lyhux.mybatiscrud.builder.grammar.update.AssignListExpr;

import java.util.ArrayList;

public class MysqlGrammar extends Grammar {

    @Override
    public String getEscapeChars() {
        return "`";
    }

    @Override
    public String escapeRawStr(ExprStr expr) {
        switch (expr)
        {
            case RawStr s -> {
                return s.getValue();
            }
            case EscapedStr es -> {
                return "'" + es.getValue() + "'";
            }
        }
    }
}
