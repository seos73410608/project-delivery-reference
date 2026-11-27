package com.seos.pmis.contract.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.contract.dto.request.ContractSearchRequest;
import com.seos.pmis.contract.dto.request.CreateContractRequest;
import com.seos.pmis.contract.dto.request.UpdateContractRequest;
import com.seos.pmis.contract.dto.response.ContractResponse;
import com.seos.pmis.contract.entity.Contract;
import com.seos.pmis.contract.entity.ContractStatus;
import com.seos.pmis.contract.mapper.ContractMapper;
import com.seos.pmis.contract.repository.ContractRepository;
import com.seos.pmis.contract.specification.ContractSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    /**
     * 계약 등록
     */
    @Transactional
    public ContractResponse create(CreateContractRequest request) {

        if (contractRepository.existsByContractCode(
                request.getContractCode()
        )) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        validateContractPeriod(
                request.getStartDate(),
                request.getEndDate()
        );

        Contract contract = Contract.builder()
                .contractCode(request.getContractCode())
                .contractName(request.getContractName())
                .contractType(request.getContractType())
                .status(ContractStatus.DRAFT)
                .signedDate(request.getSignedDate())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .currencyCode(request.getCurrencyCode())
                .contractAmount(request.getContractAmount())
                .description(request.getDescription())
                .build();

        Contract savedContract = contractRepository.save(contract);

        return contractMapper.toResponse(savedContract);
    }

    /**
     * 계약 단건 조회
     */
    public ContractResponse get(Long contractId) {

        Contract contract = findContract(contractId);

        return contractMapper.toResponse(contract);
    }

    /**
     * 계약 목록 검색
     */
    public Page<ContractResponse> search(
            ContractSearchRequest request,
            Pageable pageable
    ) {

        Specification<Contract> specification =
                ContractSpecification.search(request);

        return contractRepository.findAll(specification, pageable)
                .map(contractMapper::toResponse);
    }

    /**
     * 계약 정보 수정
     */
    @Transactional
    public ContractResponse update(
            Long contractId,
            UpdateContractRequest request
    ) {

        Contract contract = findContract(contractId);

        validateContractPeriod(
                request.getStartDate(),
                request.getEndDate()
        );

        contract.update(
                request.getContractName(),
                request.getContractType(),
                request.getSignedDate(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCurrencyCode(),
                request.getContractAmount(),
                request.getDescription()
        );

        return contractMapper.toResponse(contract);
    }

    /**
     * 계약 상태 변경
     */
    @Transactional
    public ContractResponse changeStatus(
            Long contractId,
            ContractStatus status
    ) {

        if (status == null) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        Contract contract = findContract(contractId);
        contract.changeStatus(status);

        return contractMapper.toResponse(contract);
    }

    /**
     * 계약 조회 공통 메서드
     */
    private Contract findContract(Long contractId) {

        return contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new BusinessException(
                                CommonErrorCode.RESOURCE_NOT_FOUND
                        )
                );
    }

    /**
     * 계약 기간 검증
     */
    private void validateContractPeriod(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate
    ) {

        if (startDate == null
                || endDate == null
                || startDate.isAfter(endDate)) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }
    }
}