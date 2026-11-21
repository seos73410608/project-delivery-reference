
package com.seos.pmis.operation.repository;

import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationParticipantRepository
        extends JpaRepository<OperationParticipant, Long> {

    List<OperationParticipant> findByOperationId(Long operationId);

    List<OperationParticipant> findByCompanyId(Long companyId);

    List<OperationParticipant> findByOperationIdAndParticipantRole(
            Long operationId,
            OperationParticipantRole participantRole
    );

    List<OperationParticipant> findByOperationIdAndEffectiveToIsNull(
            Long operationId
    );

    boolean existsByOperationIdAndCompanyIdAndOrganizationIdAndParticipantRoleAndEffectiveToIsNull(
            Long operationId,
            Long companyId,
            Long organizationId,
            OperationParticipantRole participantRole
    );

    boolean existsByOperationIdAndParticipantRoleAndPrimaryTrueAndEffectiveToIsNull(
            Long operationId,
            OperationParticipantRole participantRole
    );
}
