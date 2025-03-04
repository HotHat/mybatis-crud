package com.lyhux.sqlbuilder.grammar;

import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record TypeValue<T>(JDBCType type, T value) {

    public static TypeValue<String> of(String val) {
        return new TypeValue<String>(JDBCType.VARCHAR, val);
    }

    public static TypeValue<Integer> of(Integer val) {
        return new TypeValue<>(JDBCType.INTEGER, val);
    }

    public static TypeValue<Long> of(Long val) {
        return new TypeValue<>(JDBCType.BIGINT, val);
    }

    public static TypeValue<Float> of(Float val) {
        return new TypeValue<>(JDBCType.FLOAT, val);
    }

    public static TypeValue<Double> of(Double val) {
        return new TypeValue<>(JDBCType.DOUBLE, val);
    }

    public static TypeValue<Time> of(Time time) {
        return new TypeValue<Time>(JDBCType.TIME, time);
    }

    public static TypeValue<Date> of(Date date) {
        return new TypeValue<Date>(JDBCType.DATE, date);
    }

    public static TypeValue<Timestamp> of(Timestamp timestamp) {
        return new TypeValue<>(JDBCType.TIMESTAMP, timestamp);
    }

    public static TypeValue<Timestamp> of(LocalDateTime localDateTime) {
        var zoneId = ZoneId.systemDefault();
        return of(localDateTime, zoneId);
    }

    public static TypeValue<Timestamp> of(LocalDateTime localDateTime, ZoneId zoneId) {
        // Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        // Convert ZonedDateTime to Instant
        Instant instant = zonedDateTime.toInstant();
        return new TypeValue<>(JDBCType.TIMESTAMP, Timestamp.from(instant));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "[type=" + type.toString()
                + ", value="
                + value.toString()
                + ", valueType="
                + value.getClass().getSimpleName()
                + "]";
    }
}
