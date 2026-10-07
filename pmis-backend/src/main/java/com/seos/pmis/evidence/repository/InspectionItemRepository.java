package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.InspectionItem;
import com.seos.pmis.evidence.enums.InspectionItemResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Inspection Item Repository
 *
 * Inspection Item 데이터 접근을 담당한다.
 */
public interface InspectionItemRepository
        extends JpaRepository<InspectionItem, Long> {

    /**
     * Inspection별 점검 항목 조회
     *
     * 하나의 Inspection에는 여러 개의
     * InspectionItem이 존재할 수 있다.
     *
     * @param inspectionId Inspection ID
     * @return 해당 Inspection의 점검 항목 목록
     */
    List<InspectionItem> findAllByInspectionIdOrderBySortOrderAsc(
            Long inspectionId
    );

    /**
     * Requirement별 Inspection Item 조회
     *
     * 동일한 Evidence Requirement가
     * 여러 Inspection에서 점검될 수 있다.
     *
     * @param requirementId Evidence Requirement ID
     * @return 해당 Requirement와 연결된 점검 항목 목록
     */
    List<InspectionItem> findAllByRequirementId(
            Long requirementId
    );

    /**
     * Evidence별 Inspection Item 조회
     *
     * 특정 Evidence가 어떤 Inspection에서
     * 사용되었는지 확인할 때 사용한다.
     *
     * @param evidenceId Evidence ID
     * @return 해당 Evidence와 연결된 점검 항목 목록
     */
    List<InspectionItem> findAllByEvidenceId(
            Long evidenceId
    );

    /**
     * Inspection + 결과별 점검 항목 조회
     *
     * 예:
     * - PENDING 항목
     * - PASSED 항목
     * - FAILED 항목
     *
     * @param inspectionId Inspection ID
     * @param result 점검 결과
     * @return 조건에 해당하는 점검 항목 목록
     */
    List<InspectionItem> findAllByInspectionIdAndResult(
            Long inspectionId,
            InspectionItemResult result
    );

    /**
     * Inspection별 점검 항목 존재 여부
     *
     * @param inspectionId Inspection ID
     * @return 점검 항목 존재 여부
     */
    boolean existsByInspectionId(Long inspectionId);
}