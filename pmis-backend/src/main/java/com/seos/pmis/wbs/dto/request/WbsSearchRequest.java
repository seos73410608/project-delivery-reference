package com.seos.pmis.wbs.dto.request;

import com.seos.pmis.wbs.entity.WbsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * WBS 검색 요청
 *
 * WBS 목록 조회 및 검색에 사용되는 요청 DTO이다.
 *
 * 검색 조건:
 * - 프로젝트 ID
 * - 검색 키워드
 * - WBS 상태
 * - 상위 WBS ID
 *
 * 정렬 조건:
 * - 정렬 기준
 * - 정렬 방향
 *
 * 페이징 조건:
 * - 페이지 번호
 * - 페이지 크기
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WbsSearchRequest {

    /**
     * 프로젝트 ID
     *
     * 특정 프로젝트에 소속된 WBS만 조회한다.
     */
    private Long projectId;

    /**
     * 검색 키워드
     *
     * WBS 코드 또는 WBS명을 대상으로 검색한다.
     */
    private String keyword;

    /**
     * WBS 상태
     */
    private WbsStatus status;

    /**
     * 상위 WBS ID
     *
     * 특정 부모 WBS의 하위 WBS를 조회한다.
     *
     * null인 경우 최상위 WBS 조회 조건으로 사용할 수 있다.
     */
    private Long parentId;

    /**
     * 정렬 기준
     *
     * 허용 예:
     * - wbsCode
     * - wbsName
     * - sortOrder
     * - createdAt
     */
    @Builder.Default
    private String sortBy = "sortOrder";

    /**
     * 정렬 방향
     *
     * 허용 값:
     * - ASC
     * - DESC
     */
    @Builder.Default
    private String direction = "ASC";

    /**
     * 페이지 번호
     *
     * 0부터 시작한다.
     */
    @Builder.Default
    private int page = 0;

    /**
     * 페이지 크기
     */
    @Builder.Default
    private int size = 20;
}