
package com.seos.pmis.change.mapper;

import com.seos.pmis.change.dto.request.ChangeCreateRequest;
import com.seos.pmis.change.dto.request.ChangeUpdateRequest;
import com.seos.pmis.change.dto.response.ChangeResponse;
import com.seos.pmis.change.entity.Change;
import com.seos.pmis.project.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ChangeMapper {

    /**
     * CreateRequest → Change Entity
     *
     * Project, status, changeKey 등
     * Backend에서 관리하는 값은 Service에서 처리한다.
     */
    public Change toEntity(
            Project project,
            ChangeCreateRequest request
    ) {

        return new Change(
                project,
                request.getTitle(),
                request.getDescription(),
                null,
                request.getPriority(),
                request.getChangeType(),
                request.getImpactLevel(),
                request.getRequesterId(),
                request.getAssigneeId(),
                request.getIdentifiedDate(),
                request.getRequestedDate(),
                request.getDueDate(),
                null,
                null,
                null,
                null,
                request.getImpactAnalysis(),
                request.getImplementationPlan(),
                null,
                null,
                null
        );
    }

    /**
     * UpdateRequest → Change Entity
     */
    public void updateEntity(
            Change change,
            ChangeUpdateRequest request
    ) {

        change.update(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getChangeType(),
                request.getImpactLevel(),
                request.getRequesterId(),
                request.getAssigneeId(),
                request.getIdentifiedDate(),
                request.getRequestedDate(),
                request.getDueDate(),
                request.getImpactAnalysis(),
                request.getImplementationPlan(),
                request.getSortOrder()
        );
    }

    /**
     * Change Entity → Response DTO
     */
    public ChangeResponse toResponse(Change change) {
        return ChangeResponse.from(change);
    }
}
