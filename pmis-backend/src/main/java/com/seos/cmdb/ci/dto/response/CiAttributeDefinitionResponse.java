
package com.seos.cmdb.ci.dto.response;

import com.seos.cmdb.ci.entity.AttributeDataType;
import com.seos.cmdb.ci.entity.AttributeNodeType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CiAttributeDefinitionResponse {

    private Long id;

    private Long parentId;

    private Long ciTypeId;

    private String attributeKey;

    private String displayName;

    private AttributeNodeType nodeType;

    private AttributeDataType dataType;

    private String referenceType;

    private String displayFormat;

    private Integer displayOrder;

    private Boolean enabled;

    private String description;

    @Builder.Default
    private List<CiAttributeDefinitionResponse> children = List.of();
}
