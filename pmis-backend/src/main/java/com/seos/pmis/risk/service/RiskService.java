package com.seos.pmis.risk.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.risk.dto.request.RiskCreateRequest;
import com.seos.pmis.risk.dto.request.RiskSearchRequest;
import com.seos.pmis.risk.dto.request.RiskStatusUpdateRequest;
import com.seos.pmis.risk.dto.request.RiskUpdateRequest;
import com.seos.pmis.risk.dto.response.RiskResponse;
import com.seos.pmis.risk.entity.Risk;
import com.seos.pmis.risk.entity.RiskStatus;
import com.seos.pmis.risk.exception.code.RiskErrorCode;
import com.seos.pmis.risk.mapper.RiskMapper;
import com.seos.pmis.risk.repository.RiskRepository;
import com.seos.pmis.risk.specification.RiskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiskService {

    private static final String RISK_KEY_PREFIX = "RISK-";

    private final RiskRepository riskRepository;
    private final RiskMapper riskMapper;

    /**
     * Risk 단건 조회
     */
    public RiskResponse getRisk(
            Long riskId
    ) {

        Risk risk = findRisk(riskId);

        return riskMapper.toResponse(risk);
    }

    /**
     * Risk 전체 검색
     */
    public Page<RiskResponse> searchRisks(
            RiskSearchRequest request
    ) {

        Pageable pageable = createPageable(request);

        return riskRepository
                .findAll(
                        RiskSpecification.search(request),
                        pageable
                )
                .map(riskMapper::toResponse);
    }

    /**
     * Project별 Risk 조회
     *
     * projectId는 URL Path Parameter를 기준으로
     * 강제 적용한다.
     */
    public Page<RiskResponse> getProjectRisks(
            Long projectId,
            RiskSearchRequest request
    ) {

        Pageable pageable = createPageable(request);

        return riskRepository
                .findAll(
                        RiskSpecification.search(
                                request,
                                projectId
                        ),
                        pageable
                )
                .map(riskMapper::toResponse);
    }

    /**
     * Risk 생성
     *
     * 처리 순서:
     *
     * 1. Project별 Risk Key 생성
     * 2. Risk Entity 생성
     * 3. 기본 상태 OPEN 설정
     * 4. Risk Key 할당
     * 5. 저장
     */
    @Transactional
    public RiskResponse createRisk(
            Long projectId,
            RiskCreateRequest request
    ) {

        String riskKey = generateRiskKey(projectId);

        Risk risk = riskMapper.toEntity(
                projectId,
                request
        );

        risk.updateStatus(
                RiskStatus.OPEN,
                null
        );

        risk.assignRiskKey(riskKey);

        Risk savedRisk = riskRepository.save(risk);

        return riskMapper.toResponse(savedRisk);
    }

    /**
     * Risk 수정
     *
     * Risk Key와 Project ID는 수정하지 않는다.
     */
    @Transactional
    public RiskResponse updateRisk(
            Long riskId,
            RiskUpdateRequest request
    ) {

        Risk risk = findRisk(riskId);

        riskMapper.updateEntity(
                risk,
                request
        );

        return riskMapper.toResponse(risk);
    }

    /**
     * Risk 삭제
     */
    @Transactional
    public void deleteRisk(
            Long riskId
    ) {

        Risk risk = findRisk(riskId);

        riskRepository.delete(risk);
    }

    /**
     * Risk Status 변경
     *
     * MITIGATED 상태로 변경할 경우
     * mitigatedDate를 현재 날짜로 설정한다.
     *
     * MITIGATED 상태에서 다른 상태로 변경할 경우
     * mitigatedDate를 초기화한다.
     */
    @Transactional
    public RiskResponse updateStatus(
            Long riskId,
            RiskStatusUpdateRequest request
    ) {

        Risk risk = findRisk(riskId);

        RiskStatus status = request.getStatus();

        LocalDate mitigatedDate = risk.getMitigatedDate();

        if (status == RiskStatus.MITIGATED) {
            mitigatedDate = LocalDate.now();
        } else if (risk.getStatus() == RiskStatus.MITIGATED) {
            mitigatedDate = null;
        }

        risk.updateStatus(
                status,
                mitigatedDate
        );

        return riskMapper.toResponse(risk);
    }

    /**
     * Risk Entity 조회
     */
    private Risk findRisk(
            Long riskId
    ) {

        return riskRepository
                .findById(riskId)
                .orElseThrow(
                        () -> new BusinessException(
                                RiskErrorCode.RISK_NOT_FOUND
                        )
                );
    }

    /**
     * Project별 Risk Key 생성
     *
     * 예:
     * RISK-001
     * RISK-002
     * RISK-003
     */
    private String generateRiskKey(
            Long projectId
    ) {

        int nextNumber = riskRepository
                .findTopByProjectIdOrderByIdDesc(projectId)
                .map(this::extractRiskKeyNumber)
                .orElse(1);

        return RISK_KEY_PREFIX
                + String.format(
                        "%03d",
                        nextNumber
                );
    }

    /**
     * 기존 Risk Key에서 다음 순번 추출
     *
     * RISK-001 -> 2
     * RISK-025 -> 26
     */
    private int extractRiskKeyNumber(
            Risk risk
    ) {

        String riskKey = risk.getRiskKey();

        if (riskKey == null
                || !riskKey.startsWith(RISK_KEY_PREFIX)) {
            return 1;
        }

        try {
            return Integer.parseInt(
                    riskKey.substring(
                            RISK_KEY_PREFIX.length()
                    )
            ) + 1;

        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * Pageable 생성
     */
    private Pageable createPageable(
            RiskSearchRequest request
    ) {

        int page = Math.max(
                request.getPage(),
                0
        );

        int size = Math.min(
                Math.max(
                        request.getSize(),
                        1
                ),
                100
        );

        String sortBy = request.getSortBy();

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        Sort.Direction direction =
                request.getDirection() != null
                        ? request.getDirection()
                        : Sort.Direction.DESC;

        return PageRequest.of(
                page,
                size,
                Sort.by(
                        direction,
                        sortBy
                )
        );
    }
}