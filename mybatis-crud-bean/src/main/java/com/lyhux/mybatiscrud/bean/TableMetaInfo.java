package com.lyhux.mybatiscrud.bean;

import com.lyhux.mybatiscrud.bean.annotation.KeyType;

import java.util.HashMap;
import java.util.Map;

public class TableMetaInfo {
    String tableName;
    String tableKey;
    KeyType keyType;
    Map<String, String> fieldColumnMap;

    TableMetaInfo(String tableName, String primaryKey, KeyType idType) {
        this(tableName, primaryKey, idType, new HashMap<String, String>());
    }

    TableMetaInfo(String tableName, String primaryKey, KeyType idType, Map<String, String> fieldColumnMap) {
        this.tableName = tableName;
        this.tableKey = primaryKey;
        this.keyType = idType;
        this.fieldColumnMap = fieldColumnMap;
    }

    public void addFieldColumn(String fieldName, String columnName) {
        fieldColumnMap.put(fieldName, columnName);
    }

    public void setFieldColumn(Map<String, String> fieldColumnMap) {
        this.fieldColumnMap = fieldColumnMap;
    }

    public String getTableName() { return tableName; }
    public String getTableKey() { return tableKey; }
    public KeyType getKeyType() { return keyType; }
    public Map<String, String> getFieldColumnMap() { return fieldColumnMap; }
}
