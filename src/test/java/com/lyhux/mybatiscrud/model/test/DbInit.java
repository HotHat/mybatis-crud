package com.lyhux.mybatiscrud.model.test;

import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import com.lyhux.mybatiscrud.model.DatabaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbInit {

    static final String DB_URL = "jdbc:mysql://localhost/xapp";
    static final String USER = "root";
    static final String PASSWORD = "123456";

    public static void initDb() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        DatabaseManager.initManager(conn, new MysqlGrammar());
    }
}
