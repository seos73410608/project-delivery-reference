package com.seos.pmis.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateCompanyRequest {

    /**
     * 회사 코드
     * 예) INTERNAL-001
     */
    @NotBlank(message = "회사 코드는 필수입니다.")
    @Size(max = 50, message = "회사 코드는 50자 이하입니다.")
    private String companyCode;

    /**
     * 회사명
     */
    @NotBlank(message = "회사명은 필수입니다.")
    @Size(max = 200, message = "회사명은 200자 이하입니다.")
    private String companyName;

    /**
     * 법인명
     */
    @Size(max = 250, message = "법인명은 250자 이하입니다.")
    private String legalName;

    /**
     * 사업자등록번호
     */
    @Size(max = 30, message = "사업자등록번호는 30자 이하입니다.")
    private String businessRegistrationNumber;

    /**
     * 국가 코드
     * ISO 3166-1 alpha-3
     * 예) KOR, USA, JPN
     */
    @NotBlank(message = "국가 코드는 필수입니다.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "국가 코드는 영문 대문자 3자리여야 합니다.")
    private String countryCode = "KOR";

    /**
     * 회사 웹사이트
     */
    @Size(max = 500, message = "웹사이트 주소는 500자 이하입니다.")
    private String website;

    /**
     * 회사 설명
     */
    private String description;
}