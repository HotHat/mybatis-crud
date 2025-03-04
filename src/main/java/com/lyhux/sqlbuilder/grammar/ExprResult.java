package com.lyhux.sqlbuilder.grammar;

import java.util.List;

public record ExprResult (String statement, List<TypeValue<?>> bindings){
}
