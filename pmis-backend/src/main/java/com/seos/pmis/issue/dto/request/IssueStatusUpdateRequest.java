package com.seos.pmis.issue.dto.request;

import com.seos.pmis.issue.entity.IssueStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Issue 상태 변경 요청 DTO
 *
 * PATCH
 * /api/issues/{id}/status
 */
@Getter
@NoArgsConstructor
public class IssueStatusUpdateRequest {

    /**
     * 변경할 Issue 상태
     */
    private IssueStatus status;
}