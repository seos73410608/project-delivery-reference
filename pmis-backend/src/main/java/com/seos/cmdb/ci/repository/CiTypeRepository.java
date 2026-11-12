
package com.seos.cmdb.ci.repository;

import com.seos.cmdb.ci.entity.CiType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CiTypeRepository extends JpaRepository<CiType, Long> {

    /**
     * CI 유형 코드 조회
     */
    Optional<CiType> findByCode(String code);

    /**
     * CI 유형 코드 중복 확인
     */
    boolean existsByCode(String code);

    /**
     * 활성화된 CI 유형 전체 조회
     */
    List<CiType> findAllByEnabledTrueOrderBySortOrderAscNameAsc();

    /**
     * 활성화된 최상위 CI 유형 조회
     */
    List<CiType> findAllByParentIsNullAndEnabledTrueOrderBySortOrderAscNameAsc();

    /**
     * 특정 상위 유형의 활성화된 하위 유형 조회
     */
    List<CiType> findAllByParent_IdAndEnabledTrueOrderBySortOrderAscNameAsc(
            Long parentId
    );

    /**
     * 특정 유형을 상위 유형으로 참조하는 하위 유형 개수
     */
    long countByParent_Id(Long parentId);

    /**
     * 활성화된 하위 유형 존재 여부
     */
    boolean existsByParent_IdAndEnabledTrue(Long parentId);

    /**
     * 전체 CI 유형 조회
     * 활성화 여부와 관계없이 정렬 순서대로 조회
     */
    List<CiType> findAllByOrderBySortOrderAscNameAsc();
}
