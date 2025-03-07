package com.lyhux.mybatiscrud.builder.grammar;

import java.util.List;

public record ExprResult (String statement, List<TypeValue<?>> bindings){
}
