
package com.seos.pmis.requirement.dto.response;

import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OperationalScopeCiResponse {

    /**
     * Operational Scope CI 연결 PK
     */
    private Long id;

    /**
     * Operational Scope ID
     */
    private Long scopeId;

    /**
     * Configuration Item ID
     */
    private Long ciId;

    /**
     * CI 코드
     */
    private String ciCode;

    /**
     * CI 이름
     */
    private String ciName;

    /**
     * CI 유형 ID
     */
    private Long ciTypeId;

    /**
     * CI 유형 코드
     */
    private String ciTypeCode;

    /**
     * CI 유형명
     */
    private String ciTypeName;

    /**
     * CI 상태
     */
    private CiStatus status;

    /**
     * CI 환경
     */
    private CiEnvironment environment;

    /**
     * 연결 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 연결 수정일시
     */
    private LocalDateTime updatedAt;
}
