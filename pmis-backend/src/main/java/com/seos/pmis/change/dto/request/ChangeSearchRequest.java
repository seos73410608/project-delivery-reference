package com.seos.pmis.change.dto.request;

import com.seos.pmis.change.entity.ChangeImpactLevel;
import com.seos.pmis.change.entity.ChangePriority;
import com.seos.pmis.change.entity.ChangeStatus;
import com.seos.pmis.change.entity.ChangeType;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
public class ChangeSearchRequest {

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * 검색 키워드
     *
     * 제목, 설명, Change Key 등을 대상으로 검색
     */
    private String keyword;

    /**
     * 변경 상태
     */
    private ChangeStatus status;

    /**
     * 변경 우선순위
     */
    private ChangePriority priority;

    /**
     * 변경 유형
     */
    private ChangeType changeType;

    /**
     * 변경 영향도
     */
    private ChangeImpactLevel impactLevel;

    /**
     * 변경 요청자 ID
     */
    private Long requesterId;

    /**
     * 변경 담당자 ID
     */
    private Long assigneeId;

    /**
     * 페이지 번호
     */
    private int page = 0;

    /**
     * 페이지 크기
     */
    private int size = 20;

    /**
     * 정렬 기준 필드
     */
    private String sortBy = "id";

    /**
     * 정렬 방향
     */
    private Sort.Direction direction = Sort.Direction.DESC;
}