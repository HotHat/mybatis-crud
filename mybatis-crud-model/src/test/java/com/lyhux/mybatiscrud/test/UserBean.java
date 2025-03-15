package com.lyhux.mybatiscrud.test;

import com.lyhux.mybatiscrud.bean.annotation.KeyType;
import com.lyhux.mybatiscrud.bean.annotation.TableColumn;
import com.lyhux.mybatiscrud.bean.annotation.TableKey;
import com.lyhux.mybatiscrud.bean.annotation.TableName;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@TableName("users")
public class UserBean {
    @TableKey(type = KeyType.AUTO)
    long id;
    String username;
    String password;
    Integer gender;
    String email;
    @TableColumn("created_at")
    Timestamp createdAt;
    @TableColumn("updated_at")
    LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public String toString() {
        return "UserBean [id=" + id + ", username=" + username + ", password=" + password + ", gender="
            + gender + ", email=" + email + ", created_at=" + createdAt + ", updated_at="
            + updatedAt + "]";
    }
}
