package com.seos.pmis.company.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.company.dto.request.CompanySearchRequest;
import com.seos.pmis.company.dto.request.CreateCompanyRequest;
import com.seos.pmis.company.dto.request.UpdateCompanyRequest;
import com.seos.pmis.company.dto.response.CompanyResponse;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.mapper.CompanyMapper;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.company.specification.CompanySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    /**
     * 회사 등록
     */
    @Transactional
    public CompanyResponse create(CreateCompanyRequest request) {

        if (companyRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        if (request.getBusinessRegistrationNumber() != null
                && !request.getBusinessRegistrationNumber().isBlank()
                && companyRepository.existsByBusinessRegistrationNumber(
                        request.getBusinessRegistrationNumber()
                )) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        Company company = companyMapper.toEntity(request);
        company.changeStatus(CompanyStatus.ACTIVE);

        Company savedCompany = companyRepository.save(company);

        return companyMapper.toResponse(savedCompany);
    }

    /**
     * 회사 단건 조회
     */
    public CompanyResponse get(Long companyId) {

        Company company = findCompany(companyId);

        return companyMapper.toResponse(company);
    }

    /**
     * 회사 목록 검색
     */
    public Page<CompanyResponse> search(
            CompanySearchRequest request,
            Pageable pageable
    ) {

        Specification<Company> specification = Specification.allOf(
                CompanySpecification.companyCodeContains(
                        request.getCompanyCode()
                ),
                CompanySpecification.companyNameContains(
                        request.getCompanyName()
                ),
                CompanySpecification.businessRegistrationNumberContains(
                        request.getBusinessRegistrationNumber()
                ),
                CompanySpecification.hasCountryCode(
                        request.getCountryCode()
                ),
                CompanySpecification.hasStatus(
                        request.getStatus()
                )
        );

        return companyRepository.findAll(specification, pageable)
                .map(companyMapper::toResponse);
    }

    /**
     * 회사 정보 수정
     */
    @Transactional
    public CompanyResponse update(
            Long companyId,
            UpdateCompanyRequest request
    ) {

        Company company = findCompany(companyId);

        String businessRegistrationNumber =
                request.getBusinessRegistrationNumber();

        if (businessRegistrationNumber != null
                && !businessRegistrationNumber.isBlank()
                && !businessRegistrationNumber.equals(
                        company.getBusinessRegistrationNumber()
                )
                && companyRepository.existsByBusinessRegistrationNumber(
                        businessRegistrationNumber
                )) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        company.update(
                request.getCompanyName(),
                request.getLegalName(),
                request.getBusinessRegistrationNumber(),
                request.getCountryCode(),
                request.getWebsite(),
                request.getDescription()
        );

        return companyMapper.toResponse(company);
    }

    /**
     * 회사 상태 변경
     */
    @Transactional
    public CompanyResponse changeStatus(
            Long companyId,
            CompanyStatus status
    ) {

        Company company = findCompany(companyId);
        company.changeStatus(status);

        return companyMapper.toResponse(company);
    }

    /**
     * 회사 조회 공통 메서드
     */
    private Company findCompany(Long companyId) {

        return companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );
    }
}