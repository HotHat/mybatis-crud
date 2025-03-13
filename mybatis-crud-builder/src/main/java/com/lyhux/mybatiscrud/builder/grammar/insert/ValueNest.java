package com.lyhux.mybatiscrud.builder.grammar.insert;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

public interface ValueNest {
    void addValues(ValueGroupExpr group);
}
