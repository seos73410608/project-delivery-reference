package com.seos.pmis.project.dto.request;

import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateProjectRequest {

    /**
     * 프로젝트명
     */
    @NotBlank(message = "프로젝트명은 필수입니다.")
    @Size(max = 200, message = "프로젝트명은 200자 이하입니다.")
    private String projectName;

    /**
     * 고객사
     */
    @NotBlank(message = "고객사명은 필수입니다.")
    @Size(max = 100, message = "고객사명은 100자 이하입니다.")
    private String customerName;

    /**
     * 프로젝트 관리자(PM)
     */
    @NotBlank(message = "프로젝트 관리자는 필수입니다.")
    @Size(max = 100, message = "프로젝트 관리자명은 100자 이하입니다.")
    private String projectManager;

    /**
     * 프로젝트 설명
     */
    @Size(max = 5000, message = "프로젝트 설명은 5000자 이하입니다.")
    private String description;

    /**
     * 시작일
     */
    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    /**
     * 종료 예정일
     */
    @NotNull(message = "종료 예정일은 필수입니다.")
    private LocalDate endDate;

    /**
     * 프로젝트 상태
     */
    @NotNull(message = "프로젝트 상태는 필수입니다.")
    private ProjectStatus status;

    /**
     * 프로젝트 우선순위
     */
    @NotNull(message = "프로젝트 우선순위는 필수입니다.")
    private ProjectPriority priority;

}