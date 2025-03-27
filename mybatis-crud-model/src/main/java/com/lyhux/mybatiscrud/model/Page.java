package com.lyhux.mybatiscrud.model;

import java.util.List;

public record Page<T>(int page, int pageSize, int total, List<T> records) {
}
