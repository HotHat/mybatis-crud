package com.lyhux.mybatiscrud.model;

import java.util.List;

public record Page<T>(int page, int pageSize, int total, List<T> records) {
    public int totalPages() {
        int t = total / pageSize;
        return total % pageSize == 0 ? t : t+1;
    }
}
