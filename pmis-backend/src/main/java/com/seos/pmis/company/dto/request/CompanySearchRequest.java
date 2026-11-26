package com.seos.pmis.company.dto.request;

import com.seos.pmis.company.entity.CompanyStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanySearchRequest {

    /**
     * 회사 코드 검색
     */
    private String companyCode;

    /**
     * 회사명 검색
     */
    private String companyName;

    /**
     * 사업자등록번호 검색
     */
    private String businessRegistrationNumber;

    /**
     * 국가 코드 검색 (ISO 3166-1 alpha-3)
     */
    private String countryCode;

    /**
     * 회사 상태 검색
     */
    private CompanyStatus status;
}