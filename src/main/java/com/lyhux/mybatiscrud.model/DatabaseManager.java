package com.lyhux.mybatiscrud.model;

import com.lyhux.mybatiscrud.builder.vendor.Grammar;

import java.sql.Connection;

public class DatabaseManager {
    private static Connection connection = null;
    private static Grammar grammar = null;


    public static synchronized void initManager(Connection connection, Grammar grammar) {
        DatabaseManager.connection = connection;
        DatabaseManager.grammar = grammar;
    }

    private DatabaseManager() {
        // Private constructor
    }

    private static class DatabaseManagerHolder {
        private static final Database INSTANCE = new Database(DatabaseManager.connection, DatabaseManager.grammar);
    }


    public static Database getInstance() {
        return DatabaseManagerHolder.INSTANCE;
    }
}
