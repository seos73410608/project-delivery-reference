package com.seos.pmis.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreateCompanyRoleAssignmentRequest {

    @NotBlank(message = "회사 역할 코드는 필수입니다.")
    @Size(max = 30, message = "회사 역할 코드는 30자를 초과할 수 없습니다.")
    private String roleCode;

    @NotNull(message = "역할 시작일은 필수입니다.")
    private LocalDate effectiveFrom;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
    private String description;
}