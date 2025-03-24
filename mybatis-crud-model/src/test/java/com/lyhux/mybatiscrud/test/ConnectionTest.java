package com.lyhux.mybatiscrud.test;

import com.lyhux.mybatiscrud.builder.grammar.InsertStmt;
import com.lyhux.mybatiscrud.builder.grammar.QueryBuilder;
import com.lyhux.mybatiscrud.builder.vendor.MysqlGrammar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.*;
import java.util.HashMap;
import java.util.Map;


public class ConnectionTest {
    static final String DB_URL = "jdbc:mysql://localhost/xapp";
    static final String USER = "root";
    static final String PASSWORD = "";

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

    @Test
    public void testTimestamp() throws SQLException {
        // TimeZone newTimeZone = TimeZone.getTimeZone("America/Los_Angeles"); // Example: Pacific Time
        // TimeZone.setDefault(newTimeZone);

        var zoneId = ZoneId.systemDefault();
        var localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        // Convert ZonedDateTime to Instant
        Instant instant = zonedDateTime.toInstant();
        var timestamp =  Timestamp.from(instant);

        System.out.printf("zone id: %s\n", zoneId );
        System.out.printf("local date time: %s, timestamp: %s\n", localDateTime, timestamp);
    }

    @Test
    public void testInsertStmt() throws SQLException {
        var insert = new QueryBuilder().table("user");

        insert.columns("username", "password", "gender", "email", "created_at", "updated_at")
            .values((group -> {
                group
                    .add("super_admin")
                    .add("123456")
                    .add(1)
                    .add("mama@gmail.com")
                    .add(LocalDateTime.now())
                    // .add(Timestamp.from(Instant.now()))
                    .addNull();

            }));

        var grammar = new MysqlGrammar();
        var result = grammar.compile(insert.toInsertStmt());
        var prepare = conn.prepareStatement(result.statement(), PreparedStatement.RETURN_GENERATED_KEYS);
        // prepare.execute();

        int count = 1;
        for (var it : result.bindings()) {
            prepare.setObject(count++, it.value());
        }

        int ret = prepare.executeUpdate();
        if (ret == 1) {
            var primaryKeySet = prepare.getGeneratedKeys();
            if (primaryKeySet.next()) {
                var primaryKey = primaryKeySet.getLong(1);
                System.out.println("Primary key: " + primaryKey);
            }
        }
    }
    /*
    @Test
    public void testBuilderInert() throws SQLException {
        var builder = new Builder(conn, new MysqlGrammar());
        var primaryKey = builder.insert((insert) -> {
            insert
                .table("user")
                .columns("username", "password", "gender", "email", "created_at", "updated_at")
                .values((group -> {
                    group
                        .add("super_admin2")
                        .add("123456")
                        .add(1)
                        .add("mama@gmail.com")
                        .add(LocalDateTime.now())
                        // .add(Timestamp.from(Instant.now()))
                        .addNull();

                }));
        });

        System.out.println("Primary key: " + primaryKey);
    }

    @Test
    public void testBuilderInert2() throws SQLException {
        var builder = new Builder(conn, new MysqlGrammar());
        var primaryKey = builder.insert((insert) -> {
            insert
                .table("type_test")
                .columns("String", "Long", "Float", "Double", "Date", "Time", "LocalDateTime", "BigDecimal")
                .values((group -> {
                    group
                        .add("String type")
                        .add(123456L)
                        .add(1.00F)
                        .add(5.00D)
                        .add(Date.valueOf(LocalDate.now()))
                        .add(Time.valueOf(LocalTime.now()))
                        .add(LocalDateTime.now())
                        .add(new BigDecimal("19823.88"))
                        ;
                        // .add(Timestamp.from(Instant.now()))
                        // .addNull();

                }));
        });

        System.out.println("Primary key: " + primaryKey);
    }


    @Test
    public void testBuilderUpdate() throws SQLException {
        var builder = new Builder(conn, new MysqlGrammar());
        var primaryKey = builder.update((update) -> {
            update
                .table("user")
                .set((assign) -> {
                    assign
                        .set("username", "update_test")
                        .set("password", "666666")
                        .set("created_at", Timestamp.from(Instant.now()))
                        .setNull("updated_at");
                })
                .where((query) -> {
                    query.where("id", ">", 10);
                });
        });

        System.out.println("Primary key: " + primaryKey);
    }
    */

    @Test
    public void testFetchData() throws Exception {

        PreparedStatement stm =  conn.prepareStatement("select * from type_test where id=1 limit 1");
        // stm.setLong(1, 1212);
        ResultSet rs = stm.executeQuery();
        Map<String, Object> rowData = new HashMap<>();

        while (rs.next()) {
            var meta =rs.getMetaData();
            int numberOfColumns = meta.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                System.out.printf("%s mysql_type:%s, java_type:%s\n", meta.getColumnName(i), meta.getColumnTypeName(i), meta.getColumnClassName(i));
                var type = meta.getColumnType(i);
                var jdbcType = JDBCType.valueOf(type);
                System.out.printf("type: %s, jdbc type: %s\n", type, jdbcType);
                /*
                switch (jdbcType) {
                    case JDBCType.VARCHAR:
                        System.out.println(rs.getString(i));
                        rowData.put(meta.getColumnName(i), rs.getString(i));
                        break;
                    case JDBCType.INTEGER:
                        System.out.println(rs.getInt(i));
                        rowData.put(meta.getColumnName(i), rs.getInt(i));
                        break;

                    case JDBCType.BIGINT:
                        System.out.println(rs.getLong(i));
                        rowData.put(meta.getColumnName(i), rs.getLong(i));
                        break;

                    case JDBCType.FLOAT:
                    case JDBCType.REAL:
                        System.out.println(rs.getFloat(i));
                        rowData.put(meta.getColumnName(i), rs.getFloat(i));
                        break;

                    case JDBCType.DOUBLE:
                        System.out.println(rs.getDouble(i));
                        rowData.put(meta.getColumnName(i), rs.getDouble(i));
                        break;

                    case JDBCType.DATE:
                        System.out.println(rs.getDate(i));
                        rowData.put(meta.getColumnName(i), rs.getDate(i));
                        break;

                    case JDBCType.TIME:
                        System.out.println(rs.getTime(i));
                        rowData.put(meta.getColumnName(i), rs.getTime(i));
                        break;

                    case JDBCType.TIMESTAMP:
                        System.out.println(rs.getObject(i));
                        rowData.put(meta.getColumnName(i), rs.getObject(i));
                        break;
                    case JDBCType.DECIMAL:
                        System.out.println(rs.getBigDecimal(i));
                        rowData.put(meta.getColumnName(i), rs.getBigDecimal(i));
                        break;
                }
                 */
                rowData.put(meta.getColumnName(i), rs.getObject(i));
            }


            System.out.printf("rowData: %s\n", rowData);
            // TypeTestBean typeTestBean = BeanMapUtil.mapToBean(rowData, TypeTestBean.class);

            // System.out.printf("bean: %s\n", typeTestBean);
        }
    }

    /*
    @Test
    public void testTranslation() throws Exception {
        conn.setAutoCommit(false);
        var rowCount = new Builder(conn, new MysqlGrammar()).update((update) -> {
            update
                .table("users")
                .set((assign) -> {
                    assign
                        .set("username", "trans111");
                })
                .where((query) -> {
                    query.where("id", 6);
                });
        });

        System.out.println("Primary key: " + rowCount);

        // if (rowCount == 1) {
        //     throw new Exception("test translation failed");
        // }

        rowCount = new Builder(conn, new MysqlGrammar()).update((update) -> {
            update
                .table("users")
                .set((assign) -> {
                    assign
                        .set("username", "trans222");
                })
                .where((query) -> {
                    query.where("id", 7);
                });
        });

        System.out.println("Primary key: " + rowCount);


        conn.commit();

        // after transaction commit will auto commit?? No! should turn on manually
        conn.setAutoCommit(true);
        rowCount = new Builder(conn, new MysqlGrammar()).update((update) -> {
            update
                .table("users")
                .set((assign) -> {
                    assign
                        .set("username", "trans333");
                })
                .where((query) -> {
                    query.where("id", 8);
                });
        });

        System.out.println("Primary key: " + rowCount);

    }
     */
}
