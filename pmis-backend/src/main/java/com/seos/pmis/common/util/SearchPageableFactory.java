package com.seos.pmis.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class SearchPageableFactory {

    private SearchPageableFactory() {
    }

    public static Pageable create(
            Integer page,
            Integer size,
            String sortBy,
            Sort.Direction direction
    ) {
        int pageNumber =
                page != null && page >= 0
                        ? page
                        : 0;

        int pageSize =
                size != null && size > 0
                        ? size
                        : 20;

        String property =
                sortBy != null && !sortBy.isBlank()
                        ? sortBy
                        : "id";

        Sort.Direction sortDirection =
                direction != null
                        ? direction
                        : Sort.Direction.ASC;

        return PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(sortDirection, property)
        );
    }
}