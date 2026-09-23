package com.seos.pmis.common.search;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * 공통 Search Pageable 생성 Factory
 *
 * Project-scoped Domain Search에서 사용하는
 * page / size / sortBy / direction 처리 규칙을 공통화한다.
 *
 * Domain별 기본 정렬 및 허용 정렬 필드는
 * 호출 측에서 전달한다.
 */
public final class SearchPageableFactory {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;

    private SearchPageableFactory() {
    }

    /**
     * Pageable 생성
     *
     * @param page               페이지 번호 (0-based)
     * @param size               페이지 크기
     * @param sortBy             정렬 필드
     * @param direction          정렬 방향 (ASC / DESC)
     * @param defaultSortBy      Domain 기본 정렬 필드
     * @param defaultDirection   Domain 기본 정렬 방향
     * @param allowedSortFields  Domain 허용 정렬 필드
     * @return Pageable
     */
    public static Pageable create(
            Integer page,
            Integer size,
            String sortBy,
            String direction,
            String defaultSortBy,
            Sort.Direction defaultDirection,
            Set<String> allowedSortFields
    ) {

        int normalizedPage =
                normalizePage(page);

        int normalizedSize =
                normalizeSize(size);

        String normalizedSortBy =
                normalizeSortBy(
                        sortBy,
                        defaultSortBy,
                        allowedSortFields
                );

        Sort.Direction normalizedDirection =
                normalizeDirection(
                        direction,
                        defaultDirection
                );

        return PageRequest.of(
                normalizedPage,
                normalizedSize,
                Sort.by(
                        normalizedDirection,
                        normalizedSortBy
                )
        );
    }

    /**
     * 페이지 번호 처리
     *
     * null이면 기본값 0을 사용한다.
     *
     * 음수는 공통 Search Contract에 따라
     * 잘못된 요청으로 처리한다.
     */
    private static int normalizePage(
            Integer page
    ) {

        if (page == null) {
            return DEFAULT_PAGE;
        }

        if (page < 0) {
            throw new IllegalArgumentException(
                    "page는 0 이상이어야 합니다."
            );
        }

        return page;
    }

    /**
     * 페이지 크기 처리
     *
     * null이면 기본값 20을 사용한다.
     *
     * 허용 범위:
     * 1 ~ 100
     */
    private static int normalizeSize(
            Integer size
    ) {

        if (size == null) {
            return DEFAULT_SIZE;
        }

        if (size < MIN_SIZE
                || size > MAX_SIZE) {

            throw new IllegalArgumentException(
                    "size는 "
                            + MIN_SIZE
                            + " 이상 "
                            + MAX_SIZE
                            + " 이하이어야 합니다."
            );
        }

        return size;
    }

    /**
     * 정렬 필드 처리
     *
     * sortBy가 없으면 Domain 기본 정렬 필드를 사용한다.
     *
     * 허용되지 않은 필드는 잘못된 요청으로 처리한다.
     */
    private static String normalizeSortBy(
            String sortBy,
            String defaultSortBy,
            Set<String> allowedSortFields
    ) {

        if (sortBy == null
                || sortBy.isBlank()) {

            return defaultSortBy;
        }

        if (allowedSortFields == null
                || !allowedSortFields.contains(sortBy)) {

            throw new IllegalArgumentException(
                    "허용되지 않은 정렬 필드입니다. sortBy="
                            + sortBy
            );
        }

        return sortBy;
    }

    /**
     * 정렬 방향 처리
     *
     * direction이 없으면 Domain 기본 방향을 사용한다.
     */
    private static Sort.Direction normalizeDirection(
            String direction,
            Sort.Direction defaultDirection
    ) {

        if (direction == null
                || direction.isBlank()) {

            return defaultDirection;
        }

        try {

            return Sort.Direction.valueOf(
                    direction.trim().toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "direction은 ASC 또는 DESC이어야 합니다."
            );
        }
    }
}