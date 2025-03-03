package com.lyhux.sqlbuilder.grammar.select;

import com.lyhux.sqlbuilder.grammar.ExprStr;

public record OrderByItem(ExprStr column, String order) {

}
