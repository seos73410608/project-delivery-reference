package com.seos.pmis.wbs.dto.response;

import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.entity.WbsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WBS 응답
 *
 * WBS 조회 결과를 API Response 형태로 전달한다.
 *
 * Entity의 연관관계 객체(Project, Parent Wbs)는
 * 직접 노출하지 않고 ID 형태로 반환한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WbsResponse {

    /**
     * WBS PK
     */
    private Long id;

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * 상위 WBS ID
     *
     * 최상위 WBS인 경우 null이다.
     */
    private Long parentId;

    /**
     * WBS 코드
     *
     * 예:
     * 1
     * 1.1
     * 1.1.1
     */
    private String wbsCode;

    /**
     * WBS 명
     */
    private String wbsName;

    /**
     * WBS Level
     */
    private Integer level;

    /**
     * 동일 부모 내 표시 순서
     */
    private Integer sortOrder;

    /**
     * WBS 상태
     */
    private WbsStatus status;

    /**
     * 설명
     */
    private String description;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * Wbs Entity → WbsResponse 변환
     *
     * @param wbs WBS Entity
     * @return WBS Response
     */
    public static WbsResponse from(Wbs wbs) {

        return WbsResponse.builder()
                .id(wbs.getId())
                .projectId(
                        wbs.getProject() != null
                                ? wbs.getProject().getId()
                                : null
                )
                .parentId(
                        wbs.getParent() != null
                                ? wbs.getParent().getId()
                                : null
                )
                .wbsCode(wbs.getWbsCode())
                .wbsName(wbs.getWbsName())
                .level(wbs.getLevel())
                .sortOrder(wbs.getSortOrder())
                .status(wbs.getStatus())
                .description(wbs.getDescription())
                .createdAt(wbs.getCreatedAt())
                .updatedAt(wbs.getUpdatedAt())
                .build();
    }
}