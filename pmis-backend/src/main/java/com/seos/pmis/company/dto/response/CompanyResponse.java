package com.seos.pmis.company.dto.response;

import com.seos.pmis.company.entity.CompanyStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CompanyResponse {

    /**
     * 회사 ID
     */
    private Long id;

    /**
     * 회사 코드
     */
    private String companyCode;

    /**
     * 회사명
     */
    private String companyName;

    /**
     * 법인명
     */
    private String legalName;

    /**
     * 사업자등록번호
     */
    private String businessRegistrationNumber;

    /**
     * 국가 코드 (ISO 3166-1 alpha-3)
     */
    private String countryCode;

    /**
     * 회사 웹사이트
     */
    private String website;

    /**
     * 회사 상태
     */
    private CompanyStatus status;

    /**
     * 회사 설명
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