
package com.seos.pmis.operation.repository;

import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OperationParticipantRepository
                extends JpaRepository<OperationParticipant, Long> {

        List<OperationParticipant> findByOperationId(Long operationId);

        List<OperationParticipant> findByCompanyId(Long companyId);

        List<OperationParticipant> findByOperationIdAndParticipantRole(
                        Long operationId,
                        OperationParticipantRole participantRole);

        List<OperationParticipant> findByOperationIdAndEffectiveToIsNull(
                        Long operationId);

        /**
         * 인계 대상 운영 참여자 조회
         *
         * OPERATOR 또는 SERVICE_PROVIDER 역할만 조회합니다.
         * 예정 인계일이 지정되지 않은 경우 유효기간을 필터링하지 않습니다.
         *
         * @param operationId 운영 서비스 ID
         * @return 인계 대상 참여자 목록
         */
        @Query("""
                        SELECT participant
                        FROM OperationParticipant participant
                        WHERE participant.operation.id = :operationId
                          AND participant.participantRole IN (
                              com.seos.pmis.operation.entity.OperationParticipantRole.OPERATOR,
                              com.seos.pmis.operation.entity.OperationParticipantRole.SERVICE_PROVIDER
                          )
                        ORDER BY participant.primary DESC, participant.id ASC
                        """)
        List<OperationParticipant> findHandoverEligibleParticipants(
                        @Param("operationId") Long operationId);

        /**
         * 예정 인계일 기준 인계 대상 운영 참여자 조회
         *
         * OPERATOR 또는 SERVICE_PROVIDER 역할 중
         * 예정 인계일이 유효기간에 포함되는 참여자만 조회합니다.
         * 유효기간 시작일과 종료일은 모두 포함합니다.
         *
         * @param operationId         운영 서비스 ID
         * @param plannedHandoverDate 예정 인계일
         * @return 인계 대상 참여자 목록
         */
        @Query("""
                        SELECT participant
                        FROM OperationParticipant participant
                        WHERE participant.operation.id = :operationId
                          AND participant.participantRole IN (
                              com.seos.pmis.operation.entity.OperationParticipantRole.OPERATOR,
                              com.seos.pmis.operation.entity.OperationParticipantRole.SERVICE_PROVIDER
                          )
                          AND (
                              participant.effectiveFrom IS NULL
                              OR participant.effectiveFrom <= :plannedHandoverDate
                          )
                          AND (
                              participant.effectiveTo IS NULL
                              OR participant.effectiveTo >= :plannedHandoverDate
                          )
                        ORDER BY participant.primary DESC, participant.id ASC
                        """)
        List<OperationParticipant> findHandoverEligibleParticipantsOnDate(
                        @Param("operationId") Long operationId,
                        @Param("plannedHandoverDate") LocalDate plannedHandoverDate);

        boolean existsByOperationIdAndCompanyIdAndOrganizationIdAndParticipantRoleAndEffectiveToIsNull(
                        Long operationId,
                        Long companyId,
                        Long organizationId,
                        OperationParticipantRole participantRole);

        boolean existsByOperationIdAndParticipantRoleAndPrimaryTrueAndEffectiveToIsNull(
                        Long operationId,
                        OperationParticipantRole participantRole);
}
