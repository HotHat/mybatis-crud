package com.lyhux.mybatiscrud.model;

public interface Model<T> {
    public void insert(T t);
    default void insertGetId(T t) {}
}
