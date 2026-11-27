package com.seos.pmis.company.dto.response;

import com.seos.pmis.company.entity.OrganizationStatus;
import com.seos.pmis.company.entity.OrganizationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrganizationResponse {

    /**
     * 조직 ID
     */
    private Long id;

    /**
     * 회사 ID
     */
    private Long companyId;

    /**
     * 회사 코드
     */
    private String companyCode;

    /**
     * 회사명
     */
    private String companyName;

    /**
     * 조직 코드
     */
    private String organizationCode;

    /**
     * 조직명
     */
    private String organizationName;

    /**
     * 조직 유형
     */
    private OrganizationType organizationType;

    /**
     * 상위 조직 ID
     */
    private Long parentOrganizationId;

    /**
     * 상위 조직명
     */
    private String parentOrganizationName;

    /**
     * 조직 책임자 사용자 ID
     */
    private Long managerUserId;

    /**
     * 조직 책임자 이름
     */
    private String managerName;

    /**
     * 조직 상태
     */
    private OrganizationStatus status;

    /**
     * 조직 시작일
     */
    private LocalDate effectiveFrom;

    /**
     * 조직 종료일
     */
    private LocalDate effectiveTo;

    /**
     * 조직 설명
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
}