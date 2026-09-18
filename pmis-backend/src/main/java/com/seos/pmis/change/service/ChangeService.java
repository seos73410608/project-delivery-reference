package com.seos.pmis.change.service;

import com.seos.pmis.change.dto.request.ChangeApprovalRequest;
import com.seos.pmis.change.dto.request.ChangeCreateRequest;
import com.seos.pmis.change.dto.request.ChangeSearchRequest;
import com.seos.pmis.change.dto.request.ChangeStatusUpdateRequest;
import com.seos.pmis.change.dto.request.ChangeUpdateRequest;
import com.seos.pmis.change.dto.response.ChangeResponse;
import com.seos.pmis.change.entity.Change;
import com.seos.pmis.change.entity.ChangeStatus;
import com.seos.pmis.change.exception.code.ChangeErrorCode;
import com.seos.pmis.change.mapper.ChangeMapper;
import com.seos.pmis.change.repository.ChangeRepository;
import com.seos.pmis.change.specification.ChangeSpecification;
import com.seos.pmis.common.exception.BusinessException;
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
public class ChangeService {

    private static final String CHANGE_KEY_PREFIX = "CHG-";

    private final ChangeRepository changeRepository;
    private final ChangeMapper changeMapper;

    /**
     * Change 단건 조회
     */
    public ChangeResponse getChange(Long changeId) {

        Change change = findChange(changeId);

        return changeMapper.toResponse(change);
    }

    /**
     * 전체 Change 검색
     */
    public Page<ChangeResponse> searchChanges(
            ChangeSearchRequest request
    ) {

        Pageable pageable = createPageable(request);

        return changeRepository
                .findAll(
                        ChangeSpecification.search(request),
                        pageable
                )
                .map(changeMapper::toResponse);
    }

    /**
     * 프로젝트별 Change 조회
     */
    public Page<ChangeResponse> getProjectChanges(
            Long projectId,
            ChangeSearchRequest request
    ) {

        Pageable pageable = createPageable(request);

        return changeRepository
                .findAll(
                        ChangeSpecification.search(request, projectId),
                        pageable
                )
                .map(changeMapper::toResponse);
    }

    /**
     * Change 생성
     */
    @Transactional
    public ChangeResponse createChange(
            Long projectId,
            ChangeCreateRequest request
    ) {

        validateDateRange(
                request.getIdentifiedDate(),
                request.getRequestedDate(),
                request.getDueDate()
        );

        String changeKey = generateChangeKey(projectId);

        Change change = changeMapper.toEntity(
                projectId,
                request
        );

        /*
         * 신규 Change의 초기 상태
         */
        change.updateStatus(
                ChangeStatus.REQUESTED,
                null,
                null,
                null,
                null
        );

        /*
         * 프로젝트별 Change Key
         *
         * 예:
         * CHG-001
         * CHG-002
         * CHG-003
         */
        change.assignChangeKey(changeKey);

        Change savedChange = changeRepository.save(change);

        return changeMapper.toResponse(savedChange);
    }

    /**
     * Change 수정
     */
    @Transactional
    public ChangeResponse updateChange(
            Long changeId,
            ChangeUpdateRequest request
    ) {

        Change change = findChange(changeId);

        validateDateRange(
                request.getIdentifiedDate(),
                request.getRequestedDate(),
                request.getDueDate()
        );

        validateUpdate(change);

        changeMapper.updateEntity(
                change,
                request
        );

        return changeMapper.toResponse(change);
    }

    /**
     * Change 삭제
     */
    @Transactional
    public void deleteChange(Long changeId) {

        Change change = findChange(changeId);

        changeRepository.delete(change);
    }

    /**
     * Change 상태 변경
     */
    @Transactional
    public ChangeResponse updateStatus(
            Long changeId,
            ChangeStatusUpdateRequest request
    ) {

        Change change = findChange(changeId);

        ChangeStatus currentStatus = change.getStatus();
        ChangeStatus newStatus = request.getStatus();

        validateStatusTransition(
                currentStatus,
                newStatus
        );

        LocalDate approvedDate = change.getApprovedDate();
        LocalDate implementedDate = change.getImplementedDate();
        LocalDate verifiedDate = change.getVerifiedDate();
        LocalDate closedDate = change.getClosedDate();

        /*
         * 상태별 Backend 관리 날짜 처리
         */
        if (newStatus == ChangeStatus.APPROVED) {
            approvedDate = LocalDate.now();
        }

        if (newStatus == ChangeStatus.IMPLEMENTING) {
            implementedDate = LocalDate.now();
        }

        if (newStatus == ChangeStatus.VERIFIED) {
            validateImplementedDate(change);
            verifiedDate = LocalDate.now();
        }

        if (newStatus == ChangeStatus.CLOSED) {
            validateVerifiedDate(change);
            closedDate = LocalDate.now();
        }

        /*
         * 승인/반려는 별도 Approval API를 사용한다.
         */
        if (newStatus == ChangeStatus.APPROVED
                || newStatus == ChangeStatus.REJECTED) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }

        change.updateStatus(
                newStatus,
                approvedDate,
                implementedDate,
                verifiedDate,
                closedDate
        );

        return changeMapper.toResponse(change);
    }

    /**
     * 승인 / 반려 처리
     *
     * 현재 상태가 PENDING_APPROVAL일 때만 가능
     */
    @Transactional
    public ChangeResponse updateApproval(
            Long changeId,
            ChangeApprovalRequest request
    ) {

        Change change = findChange(changeId);

        if (change.getStatus() != ChangeStatus.PENDING_APPROVAL) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_APPROVAL
            );
        }

        if (request.getApproved() == null) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_APPROVAL
            );
        }

        ChangeStatus newStatus =
                request.getApproved()
                        ? ChangeStatus.APPROVED
                        : ChangeStatus.REJECTED;

        /*
         * 승인일은 승인 시점에만 기록
         */
        LocalDate approvedDate =
                request.getApproved()
                        ? LocalDate.now()
                        : null;

        change.updateApproval(
                newStatus,
                approvedDate,
                request.getApprovalComment()
        );

        return changeMapper.toResponse(change);
    }

    /**
     * Change 조회
     */
    private Change findChange(Long changeId) {

        return changeRepository
                .findById(changeId)
                .orElseThrow(
                        () -> new BusinessException(
                                ChangeErrorCode.CHANGE_NOT_FOUND
                        )
                );
    }

    /**
     * 프로젝트별 Change Key 생성
     *
     * 예:
     * 기존 CHG-001 → CHG-002
     * 기존 CHG-002 → CHG-003
     */
    private String generateChangeKey(Long projectId) {

        int nextNumber =
                changeRepository
                        .findTopByProjectIdOrderByIdDesc(projectId)
                        .map(this::extractChangeKeyNumber)
                        .orElse(1);

        return CHANGE_KEY_PREFIX
                + String.format("%03d", nextNumber);
    }

    /**
     * 기존 Change Key에서 번호 추출
     */
    private int extractChangeKeyNumber(Change change) {

        String changeKey = change.getChangeKey();

        if (changeKey == null
                || !changeKey.startsWith(CHANGE_KEY_PREFIX)) {

            return 1;
        }

        try {

            return Integer.parseInt(
                    changeKey.substring(
                            CHANGE_KEY_PREFIX.length()
                    )
            ) + 1;

        } catch (NumberFormatException e) {

            return 1;
        }
    }

    /**
     * 날짜 범위 검증
     *
     * identifiedDate
     *      <= requestedDate
     *      <= dueDate
     */
    private void validateDateRange(
            LocalDate identifiedDate,
            LocalDate requestedDate,
            LocalDate dueDate
    ) {

        if (identifiedDate != null
                && requestedDate != null
                && identifiedDate.isAfter(requestedDate)) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_DATE_RANGE
            );
        }

        if (requestedDate != null
                && dueDate != null
                && requestedDate.isAfter(dueDate)) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_DATE_RANGE
            );
        }

        if (identifiedDate != null
                && dueDate != null
                && identifiedDate.isAfter(dueDate)) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_DATE_RANGE
            );
        }
    }

    /**
     * 상태 전이 검증
     */
    private void validateStatusTransition(
            ChangeStatus currentStatus,
            ChangeStatus newStatus
    ) {

        if (currentStatus == null
                || newStatus == null) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }

        if (currentStatus == newStatus) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }

        /*
         * 승인 / 반려는 Approval API에서 처리
         */
        if (newStatus == ChangeStatus.APPROVED
                || newStatus == ChangeStatus.REJECTED) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }

        /*
         * 종료 상태에서는 추가 상태 변경 불가
         */
        if (currentStatus == ChangeStatus.CLOSED
                || currentStatus == ChangeStatus.REJECTED
                || currentStatus == ChangeStatus.CANCELLED) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }

        /*
         * REQUESTED
         */
        if (currentStatus == ChangeStatus.REQUESTED) {

            if (newStatus != ChangeStatus.ANALYZING
                    && newStatus != ChangeStatus.CANCELLED) {

                throw new BusinessException(
                        ChangeErrorCode.CHANGE_INVALID_STATUS
                );
            }

            return;
        }

        /*
         * ANALYZING
         */
        if (currentStatus == ChangeStatus.ANALYZING) {

            if (newStatus != ChangeStatus.PENDING_APPROVAL
                    && newStatus != ChangeStatus.CANCELLED) {

                throw new BusinessException(
                        ChangeErrorCode.CHANGE_INVALID_STATUS
                );
            }

            return;
        }

        /*
         * PENDING_APPROVAL
         *
         * APPROVED / REJECTED는
         * Approval API에서 처리
         */
        if (currentStatus == ChangeStatus.PENDING_APPROVAL) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }

        /*
         * APPROVED
         */
        if (currentStatus == ChangeStatus.APPROVED) {

            if (newStatus != ChangeStatus.IMPLEMENTING
                    && newStatus != ChangeStatus.CANCELLED) {

                throw new BusinessException(
                        ChangeErrorCode.CHANGE_INVALID_STATUS
                );
            }

            return;
        }

        /*
         * IMPLEMENTING
         */
        if (currentStatus == ChangeStatus.IMPLEMENTING) {

            if (newStatus != ChangeStatus.VERIFIED) {

                throw new BusinessException(
                        ChangeErrorCode.CHANGE_INVALID_STATUS
                );
            }

            return;
        }

        /*
         * VERIFIED
         */
        if (currentStatus == ChangeStatus.VERIFIED) {

            if (newStatus != ChangeStatus.CLOSED) {

                throw new BusinessException(
                        ChangeErrorCode.CHANGE_INVALID_STATUS
                );
            }
        }
    }

    /**
     * 수정 가능 상태 검증
     *
     * 종료/반려/취소된 Change는 일반 수정 API로
     * 업무 내용을 변경하지 못하도록 한다.
     */
    private void validateUpdate(Change change) {

        ChangeStatus status = change.getStatus();

        if (status == ChangeStatus.CLOSED
                || status == ChangeStatus.REJECTED
                || status == ChangeStatus.CANCELLED) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }
    }

    /**
     * VERIFIED 전환 전 구현일 검증
     */
    private void validateImplementedDate(Change change) {

        if (change.getImplementedDate() == null) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }
    }

    /**
     * CLOSED 전환 전 검증일 검증
     */
    private void validateVerifiedDate(Change change) {

        if (change.getVerifiedDate() == null) {

            throw new BusinessException(
                    ChangeErrorCode.CHANGE_INVALID_STATUS
            );
        }
    }

    /**
     * Pageable 생성
     */
    private Pageable createPageable(
            ChangeSearchRequest request
    ) {

        int page =
                Math.max(
                        request.getPage(),
                        0
                );

        int size =
                Math.min(
                        Math.max(
                                request.getSize(),
                                1
                        ),
                        100
                );

        String sortBy = request.getSortBy();

        if (sortBy == null
                || sortBy.isBlank()) {

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