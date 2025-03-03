package com.lyhux.sqlbuilder.grammar;

import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record ExprValue<T>(JDBCType type, T value) {

    public static ExprValue<String> of(String val) {
        return new ExprValue<String>(JDBCType.VARCHAR, val);
    }

    public static ExprValue<Integer> of(Integer val) {
        return new ExprValue<>(JDBCType.INTEGER, val);
    }

    public static ExprValue<Long> of(Long val) {
        return new ExprValue<>(JDBCType.BIGINT, val);
    }

    public static ExprValue<Float> of(Float val) {
        return new ExprValue<>(JDBCType.FLOAT, val);
    }

    public static ExprValue<Double> of(Double val) {
        return new ExprValue<>(JDBCType.DOUBLE, val);
    }

    public static ExprValue<Time> of(Time time) {
        return new ExprValue<Time>(JDBCType.TIME, time);
    }

    public static ExprValue<Date> of(Date date) {
        return new ExprValue<Date>(JDBCType.DATE, date);
    }

    public static ExprValue<Timestamp> of(Timestamp timestamp) {
        return new ExprValue<>(JDBCType.TIMESTAMP, timestamp);
    }

    public static ExprValue<Timestamp> of(LocalDateTime localDateTime) {
        var zoneId = ZoneId.systemDefault();
        return of(localDateTime, zoneId);
    }

    public static ExprValue<Timestamp> of(LocalDateTime localDateTime, ZoneId zoneId) {
        // Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        // Convert ZonedDateTime to Instant
        Instant instant = zonedDateTime.toInstant();
        return new ExprValue<>(JDBCType.TIMESTAMP, Timestamp.from(instant));
    }

}
