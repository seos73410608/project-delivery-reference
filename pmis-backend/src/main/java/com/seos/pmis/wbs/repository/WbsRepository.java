package com.seos.pmis.wbs.repository;

import com.seos.pmis.wbs.entity.Wbs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * WBS Repository
 *
 * WBS Entity에 대한 기본 CRUD 및
 * 프로젝트 / 계층 구조 기반 조회 기능을 제공한다.
 *
 * JpaSpecificationExecutor를 통해
 * WbsSpecification과 연계한 동적 검색을 지원한다.
 *
 * WBS 수정은 Repository의 별도 update 메서드를 사용하지 않고
 * Service에서 Entity의 상태를 변경한 후
 * JPA Dirty Checking을 통해 반영한다.
 */
public interface WbsRepository
        extends JpaRepository<Wbs, Long>,
                JpaSpecificationExecutor<Wbs> {

    /**
     * 프로젝트별 WBS 조회
     *
     * 동일 프로젝트에 속한 모든 WBS를
     * sortOrder 오름차순으로 조회한다.
     *
     * @param projectId 프로젝트 ID
     * @return 프로젝트 WBS 목록
     */
    List<Wbs> findByProjectIdOrderBySortOrderAsc(
            Long projectId
    );

    /**
     * 프로젝트의 최상위 WBS 조회
     *
     * parent가 없는 WBS만 조회한다.
     *
     * @param projectId 프로젝트 ID
     * @return 최상위 WBS 목록
     */
    List<Wbs> findByProjectIdAndParentIsNullOrderBySortOrderAsc(
            Long projectId
    );

    /**
     * 특정 부모 WBS의 하위 WBS 조회
     *
     * 현재 부모의 직계 하위 WBS만 조회한다.
     *
     * 하위 WBS의 Level 재계산에도 사용한다.
     *
     * @param parentId 부모 WBS ID
     * @return 하위 WBS 목록
     */
    List<Wbs> findByParentIdOrderBySortOrderAsc(
            Long parentId
    );

    /**
     * 프로젝트 + WBS 코드 조회
     *
     * WBS Code는 프로젝트 내부에서 유일하다.
     *
     * @param projectId 프로젝트 ID
     * @param wbsCode WBS 코드
     * @return WBS
     */
    Optional<Wbs> findByProjectIdAndWbsCode(
            Long projectId,
            String wbsCode
    );

    /**
     * 프로젝트 내 WBS 코드 존재 여부
     *
     * WBS 생성 및 수정 시
     * WBS Code 중복 검증에 사용한다.
     *
     * @param projectId 프로젝트 ID
     * @param wbsCode WBS 코드
     * @return 존재 여부
     */
    boolean existsByProjectIdAndWbsCode(
            Long projectId,
            String wbsCode
    );

    /**
     * 특정 WBS의 하위 WBS 존재 여부
     *
     * WBS 삭제 가능 여부 판단에 사용한다.
     *
     * 하위 WBS가 존재하면
     * 현재 WBS를 삭제할 수 없다.
     *
     * @param parentId 부모 WBS ID
     * @return 하위 WBS 존재 여부
     */
    boolean existsByParentId(
            Long parentId
    );

    /**
     * 프로젝트의 WBS 존재 여부
     *
     * 프로젝트 삭제 또는
     * 프로젝트별 WBS 존재 여부 확인 등에 사용한다.
     *
     * @param projectId 프로젝트 ID
     * @return WBS 존재 여부
     */
    boolean existsByProjectId(
            Long projectId
    );

    /**
     * 특정 WBS가 프로젝트에 소속되어 있는지 확인
     *
     * Schedule 생성 및 수정 시
     * 전달받은 projectId와 wbsId의 소속 관계를 검증하는 데 사용한다.
     *
     * Schedule이 특정 Project에 연결될 때
     * 해당 WBS가 다른 Project의 WBS인지 확인하기 위한 메서드이다.
     *
     * @param projectId 프로젝트 ID
     * @param id WBS ID
     * @return 해당 프로젝트에 WBS가 존재하면 true
     */
    boolean existsByProjectIdAndId(
            Long projectId,
            Long id
    );
}