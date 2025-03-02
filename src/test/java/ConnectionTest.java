package test.java;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class ConnectionTest {
    static final String DB_URL = "jdbc:mysql://localhost/xapp";
    static final String USER = "root";
    static final String PASSWORD = "123456";

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

       PreparedStatement stm =  conn.prepareStatement("select * from users where id in (?, ?)");
        //       Array arr = conn.createArrayOf("INTEGER", new Object[]{"1", "2", "3"});
        // tm.setArray(1, arr);
        // stm.setString(1, "31");
        // stm.setString(2, "32");
        // stm.setString(3, "33");
        stm.setInt(1, 1);
        stm.setInt(2, 2);
       ResultSet rs = stm.executeQuery();
       while (rs.next()) {
           System.out.printf("%s %s\n", rs.getInt(1), rs.getString(2));
       }
    }

    @Test
    public void testType() throws SQLException {
        PreparedStatement stm =  conn.prepareStatement("select * from type_test where `bigint` in (?) limit 1");
        stm.setLong(1, 1212);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            var meta =rs.getMetaData();
            int numberOfColumns = meta.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                System.out.printf("%s mysql_type:%s, java_type:%s\n", meta.getColumnName(i), meta.getColumnTypeName(i), meta.getColumnClassName(i));
            }

//            var meta =rs.getMetaData().getColumnType(1);
//            System.out.println("type: %s" + rs.getMetaData());
//            System.out.printf("%s %s\n", rs.getInt(1), rs.getString(2));
        }
    }

    @Test
    public void testInsert() throws SQLException {
        PreparedStatement stm =  conn.prepareStatement(
                "insert into type_test ( `char`, `date`, `datetime`, `decimal`, `float`, `longtext`, `blob`, `enum`) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
//        stm.setLong(9, 1227);
        stm.setString(1, "a");
        stm.setDate(2, Date.valueOf(LocalDate.now()));
//        stm.setString(3, LocalDateTime.now().toString());
        stm.setTimestamp(3, new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()));
//        stm.setString(4, "123.77");
        stm.setBigDecimal(4, new BigDecimal("12334.88"));
        stm.setFloat(5, 123.77F);
        stm.setString(6, "longtext test insert by string");
        byte[] blob = "blob input data".getBytes(StandardCharsets.UTF_8);
        stm.setBlob(7, new ByteArrayInputStream(blob));
        stm.setNull(8, Types.NULL);
        int result = stm.executeUpdate();

        if (result == 1) {
            var primaryKeySet = stm.getGeneratedKeys();
            if (primaryKeySet.next()) {
                var primaryKey = primaryKeySet.getLong(1);
                System.out.println("Primary key: " + primaryKey);
            }
        }

        System.out.printf("insert result: %d\n", result);

    }
}
