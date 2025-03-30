package com.lyhux.mybatiscrud.bean.test;

import com.lyhux.mybatiscrud.bean.annotation.KeyType;
import com.lyhux.mybatiscrud.bean.annotation.TableColumn;
import com.lyhux.mybatiscrud.bean.annotation.TableKey;
import com.lyhux.mybatiscrud.bean.annotation.TableName;

@TableName("my_bean")
public  class MyBean {
    @TableKey(type = KeyType.AUTO)
    private int id;

    @TableColumn("bean_name")
    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}