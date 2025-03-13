package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.Connection;

public class DatabaseManager {
    private static Database instance = null;
    private static Connection connection = null;
    private static Grammar grammar = null;

    public static void initManager(Connection connection, Grammar grammar) {
        DatabaseManager.connection = connection;
        DatabaseManager.grammar = grammar;
    }

    public static Database getInstance() {
        if (instance == null) {
            if (connection == null) {
                throw new RuntimeException("connection is missing");
            }
            if (grammar == null) {
                throw new RuntimeException("grammar is missing");
            }

            synchronized (DatabaseManager.class) {
                instance = new Database(DatabaseManager.connection, DatabaseManager.grammar);
            }
        }

        return instance;
    }
}
