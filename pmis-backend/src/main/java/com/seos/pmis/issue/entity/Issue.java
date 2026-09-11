package com.seos.pmis.issue.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.project.entity.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Issue Entity
 *
 * 프로젝트 수행 중 발생하는
 * 실제 문제 및 장애 사항을 관리한다.
 *
 * Domain 관계:
 *
 * Project
 *   └── Issue
 *
 * Issue는 반드시 하나의
 * Project에 소속된다.
 */
@Getter
@Entity
@Builder
@Table(
        name = "issues"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Issue extends BaseEntity {

    /**
     * Issue PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * 소속 프로젝트
     *
     * 하나의 Issue는 반드시
     * 하나의 Project에 소속된다.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;


    /**
     * Issue 식별 Key
     *
     * 예:
     *
     * ISSUE-001
     * ISSUE-002
     *
     * 현재 V1에서는
     * 자동 생성 정책을 적용하지 않는다.
     *
     * 향후 Project별 Issue Key
     * 생성 정책을 적용할 수 있다.
     */
    @Column(
            name = "issue_key",
            length = 50
    )
    private String issueKey;


    /**
     * Issue 제목
     */
    @Column(
            nullable = false,
            length = 200
    )
    private String title;


    /**
     * Issue 상세 내용
     */
    @Lob
    private String description;


    /**
     * Issue 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private IssueStatus status;


    /**
     * Issue 중요도
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private IssuePriority priority;


    /**
     * 담당자 ID
     *
     * User Domain과의 직접적인
     * Entity 연관 관계는 V1에서 사용하지 않는다.
     *
     * 향후 User Domain 구현 시
     * 관계를 확장할 수 있다.
     */
    @Column(
            name = "assignee_id"
    )
    private Long assigneeId;


    /**
     * 등록자 ID
     *
     * 향후 JWT 로그인 사용자 정보를
     * 기반으로 자동 설정할 수 있다.
     */
    @Column(
            name = "reporter_id"
    )
    private Long reporterId;


    /**
     * 실제 Issue 발생일
     *
     * createdAt과 다르다.
     *
     * occurredDate
     * → 실제 문제 발생일
     *
     * createdAt
     * → PMIS 등록일시
     */
    @Column(
            name = "occurred_date"
    )
    private LocalDate occurredDate;


    /**
     * 조치 목표일
     */
    @Column(
            name = "due_date"
    )
    private LocalDate dueDate;


    /**
     * 해결일
     *
     * Issue가 RESOLVED 상태가 된 경우
     * 설정할 수 있다.
     */
    @Column(
            name = "resolved_date"
    )
    private LocalDate resolvedDate;


    /**
     * 표시 순서
     */
    @Column(
            name = "sort_order",
            nullable = false
    )
    private Integer sortOrder;


    /**
     * Issue 기본 정보 수정
     *
     * Project는 변경하지 않는다.
     *
     * Issue Key와 Reporter는
     * 별도 정책에 따라 관리한다.
     *
     * @param title Issue 제목
     * @param description 상세 내용
     * @param status Issue 상태
     * @param priority 중요도
     * @param assigneeId 담당자 ID
     * @param occurredDate 발생일
     * @param dueDate 조치 목표일
     * @param sortOrder 표시 순서
     */
    public void update(
            String title,
            String description,
            IssueStatus status,
            IssuePriority priority,
            Long assigneeId,
            LocalDate occurredDate,
            LocalDate dueDate,
            Integer sortOrder
    ) {

        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assigneeId = assigneeId;
        this.occurredDate = occurredDate;
        this.dueDate = dueDate;
        this.sortOrder = sortOrder;
    }


    /**
     * Issue 상태 변경
     *
     * 상태 전이 규칙은
     * Service에서 검증한다.
     *
     * @param status 새로운 상태
     */
    public void changeStatus(
            IssueStatus status
    ) {

        this.status = status;
    }


    /**
     * Issue 중요도 변경
     *
     * @param priority 새로운 중요도
     */
    public void changePriority(
            IssuePriority priority
    ) {

        this.priority = priority;
    }


    /**
     * Issue 담당자 변경
     *
     * @param assigneeId 새로운 담당자 ID
     */
    public void changeAssignee(
            Long assigneeId
    ) {

        this.assigneeId = assigneeId;
    }


    /**
     * Issue 해결일 변경
     *
     * Issue가 해결된 경우
     * 해결일을 설정한다.
     *
     * @param resolvedDate 해결일
     */
    public void changeResolvedDate(
            LocalDate resolvedDate
    ) {

        this.resolvedDate = resolvedDate;
    }


    /**
     * Issue 표시 순서 변경
     *
     * @param sortOrder 새로운 표시 순서
     */
    public void changeSortOrder(
            Integer sortOrder
    ) {

        this.sortOrder = sortOrder;
    }
}