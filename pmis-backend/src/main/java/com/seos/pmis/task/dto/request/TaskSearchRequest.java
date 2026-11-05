
package com.seos.pmis.task.dto.request;

import com.seos.pmis.task.entity.TaskPriority;
import com.seos.pmis.task.entity.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Task 검색 요청 DTO
 *
 * Task 목록 조회에 필요한 검색 조건과
 * 정렬 및 페이징 정보를 전달한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskSearchRequest {

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * WBS ID
     */
    private Long wbsId;

    /**
     * Schedule ID
     */
    private Long scheduleId;

    /**
     * 담당자 User ID
     */
    private Long assigneeId;

    /**
     * Task 이름 또는 설명 검색어
     */
    private String keyword;

    /**
     * Task 상태
     */
    private TaskStatus status;

    /**
     * Task 우선순위
     */
    private TaskPriority priority;

    /**
     * 검색 시작일
     */
    private java.time.LocalDate startDate;

    /**
     * 검색 종료일
     */
    private java.time.LocalDate endDate;

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
