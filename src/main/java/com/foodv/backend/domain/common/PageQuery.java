package com.foodv.backend.domain.common;

public record PageQuery(int page, int size, String sortBy, boolean ascending) {

    public PageQuery {
        if (page < 0) page = 0;
        if (size <= 0 || size > 200) size = 20;
        if (sortBy == null || sortBy.isBlank()) sortBy = "id";
    }

    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size, "id", true);
    }

    public static PageQuery of(int page, int size, String sortBy) {
        return new PageQuery(page, size, sortBy, true);
    }
}
