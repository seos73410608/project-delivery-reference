package com.seos.cmdb.relationship.dto.response;

import com.seos.cmdb.relationship.entity.RelationshipStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CiRelationshipResponse {

    private Long id;

    private Long sourceCiId;

    private String sourceCiCode;

    private String sourceCiName;

    private Long targetCiId;

    private String targetCiCode;

    private String targetCiName;

    private Long relationshipTypeId;

    private String relationshipTypeCode;

    private String relationshipTypeName;

    private String description;

    private RelationshipStatus status;

    private LocalDateTime deactivatedAt;

    private Long deactivatedBy;

    private String deactivationReason;
}