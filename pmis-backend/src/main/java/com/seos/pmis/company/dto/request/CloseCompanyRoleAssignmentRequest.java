package com.seos.pmis.company.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CloseCompanyRoleAssignmentRequest {

    @NotNull(message = "역할 종료일은 필수입니다.")
    private LocalDate effectiveTo;
}