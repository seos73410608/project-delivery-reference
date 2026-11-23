package com.seos.pmis.projectoperation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ProjectOperationHandoverReHandoverCreateRequest {

    @NotNull(message = "프로젝트 운영 관계 ID는 필수입니다.")
    private Long projectOperationId;

    @NotNull(message = "인계 참여자 ID는 필수입니다.")
    private Long fromParticipantId;

    @NotNull(message = "인수 참여자 ID는 필수입니다.")
    private Long toParticipantId;

    private LocalDate plannedHandoverDate;

    private String notes;
}