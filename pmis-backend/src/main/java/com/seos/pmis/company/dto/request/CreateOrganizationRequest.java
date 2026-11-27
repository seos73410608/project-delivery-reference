package com.seos.pmis.company.dto.request;

import com.seos.pmis.company.entity.OrganizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateOrganizationRequest {

    /**
     * 조직 코드
     */
    @NotBlank(message = "조직 코드는 필수입니다.")
    @Size(max = 50, message = "조직 코드는 50자 이하입니다.")
    private String organizationCode;

    /**
     * 조직명
     */
    @NotBlank(message = "조직명은 필수입니다.")
    @Size(max = 200, message = "조직명은 200자 이하입니다.")
    private String organizationName;

    /**
     * 조직 유형
     */
    @NotNull(message = "조직 유형은 필수입니다.")
    private OrganizationType organizationType;

    /**
     * 상위 조직 ID
     */
    private Long parentOrganizationId;

    /**
     * 조직 책임자 사용자 ID
     */
    private Long managerUserId;

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
}