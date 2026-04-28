package com.foodv.backend.infrastructure.web.dto.common;

import com.foodv.backend.domain.common.PagedResult;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
    public static <T> PageResponse<T> from(PagedResult<T> page) {
        return new PageResponse<>(
                page.content(),
                page.totalElements(),
                page.totalPages(),
                page.currentPage(),
                page.pageSize()
        );
    }
}
