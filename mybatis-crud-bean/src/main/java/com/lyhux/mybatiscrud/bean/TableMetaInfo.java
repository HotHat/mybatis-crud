package com.lyhux.mybatiscrud.bean;

import java.util.HashMap;
import java.util.Map;

public class TableMetaInfo {
    String tableName;
    Map<String, String> fieldColumnMap;

    TableMetaInfo(String tableName) {
        this.tableName = tableName;
        fieldColumnMap = new HashMap<String, String>();
    }

    TableMetaInfo(String tableName, Map<String, String> fieldColumnMap) {
        this.tableName = tableName;
        this.fieldColumnMap = fieldColumnMap;
    }

    public void addFieldColumn(String fieldName, String columnName) {
        fieldColumnMap.put(fieldName, columnName);
    }

    public void setFieldColumn(Map<String, String> fieldColumnMap) {
        this.fieldColumnMap = fieldColumnMap;
    }

    public String getTableName() { return tableName; }
    public Map<String, String> getFieldColumnMap() { return fieldColumnMap; }
}
