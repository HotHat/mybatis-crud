package com.lyhux.mybatiscrud.bean;

import com.lyhux.mybatiscrud.bean.annotation.KeyType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TableMetaInfo {
    String tableName;
    String tableKey;
    KeyType keyType;
    String keyFieldType;
    Map<String, String> fieldColumnMap;

    TableMetaInfo(String tableName, String primaryKey, KeyType idType, String keyFieldType) {
        this(tableName, primaryKey, idType, keyFieldType, new LinkedHashMap<>());
    }

    TableMetaInfo(String tableName, String primaryKey, KeyType idType, String keyFieldType, Map<String, String> fieldColumnMap) {
        this.tableName = tableName;
        this.tableKey = primaryKey;
        this.keyType = idType;
        this.keyFieldType = keyFieldType;
        this.fieldColumnMap = fieldColumnMap;
    }

    public void addFieldColumn(String fieldName, String columnName) {
        fieldColumnMap.put(fieldName, columnName);
    }

    public void setFieldColumn(Map<String, String> fieldColumnMap) {
        this.fieldColumnMap = fieldColumnMap;
    }

    public boolean isPrimaryKeyInteger() {
        return keyFieldType.equals("java.lang.Integer")
            || keyFieldType.equals("int");
    }

    public boolean isPrimaryKeyLong() {
        return keyFieldType.equals("java.lang.Long")
            || keyFieldType.equals("long");
    }

    public boolean isPrimaryKeyString() {
        return keyFieldType.equals("java.lang.String");
    }


    public String getTableName() { return tableName; }
    public String getTableKey() { return tableKey; }
    public KeyType getKeyType() { return keyType; }
    public String getKeyFieldType() { return keyFieldType; }
    public Map<String, String> getFieldColumnMap() { return fieldColumnMap; }

    @Override
    public String toString() {
        return "TableMetaInfo [tableName=" + tableName + ", tableKey=" + tableKey
            + ", keyType=" + keyType + ", fieldColumnMap=" + fieldColumnMap + "]";
    }
}
