package com.seos.pmis.wbs.dto.request;

import com.seos.pmis.wbs.entity.WbsStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WbsUpdateRequest {

    /**
     * 상위 WBS ID
     *
     * 최상위 WBS인 경우 null
     */
    private Long parentId;

    /**
     * WBS 코드
     */
    @NotBlank(message = "WBS 코드는 필수입니다.")
    @Size(max = 50, message = "WBS 코드는 50자 이내여야 합니다.")
    private String wbsCode;

    /**
     * WBS명
     */
    @NotBlank(message = "WBS명은 필수입니다.")
    @Size(max = 200, message = "WBS명은 200자 이내여야 합니다.")
    private String wbsName;

    /**
     * WBS 설명
     */
    @Size(max = 1000, message = "WBS 설명은 1000자 이내여야 합니다.")
    private String description;

    /**
     * WBS 상태
     */
    @NotNull(message = "WBS 상태는 필수입니다.")
    private WbsStatus status;

    /**
     * 정렬 순서
     */
    @NotNull(message = "정렬 순서는 필수입니다.")
    private Integer sortOrder;
}