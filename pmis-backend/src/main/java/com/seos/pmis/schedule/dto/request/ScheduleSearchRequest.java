package com.seos.pmis.schedule.dto.request;

import com.seos.pmis.schedule.entity.ScheduleStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleSearchRequest {

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * WBS ID
     */
    private Long wbsId;

    /**
     * Schedule 명 또는 설명 검색어
     */
    private String keyword;

    /**
     * Schedule 상태
     */
    private ScheduleStatus status;

    /**
     * 정렬 필드
     */
    private String sortBy;

    /**
     * 정렬 방향
     */
    private String direction;

    /**
     * 페이지 번호
     */
    private Integer page = 0;

    /**
     * 페이지 크기
     */
    private Integer size = 20;
}