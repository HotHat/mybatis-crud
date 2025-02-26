package test.java;

import junit.framework.TestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;

public class ConnectionTest {
    static final String DB_URL = "jdbc:mysql://47.98.198.35/ziwen_vip_test";
    static final String USER = "ziwen_vip_test";
    static final String PASSWORD = "testfgg239kkdk39A";

    private Connection conn;

    @BeforeEach
    public void setUp() throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

        conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }


    @AfterEach
    protected void tearDown() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testConnection() throws SQLException {
        System.out.println("Testing connection...");

       PreparedStatement stm =  conn.prepareStatement("select * from users where id in (?, ?, ?)");
        //       Array arr = conn.createArrayOf("INTEGER", new Object[]{"1", "2", "3"});
        // tm.setArray(1, arr);
        // stm.setString(1, "31");
        // stm.setString(2, "32");
        // stm.setString(3, "33");
        stm.setInt(1, 31);
        stm.setInt(2, 32);
        stm.setInt(3, 33);
       ResultSet rs = stm.executeQuery();
       while (rs.next()) {
           System.out.printf("%s %s\n", rs.getInt(1), rs.getString(2));
       }
    }
}
