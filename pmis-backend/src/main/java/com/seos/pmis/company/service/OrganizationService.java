package com.seos.pmis.company.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.company.dto.request.CreateOrganizationRequest;
import com.seos.pmis.company.dto.request.UpdateOrganizationRequest;
import com.seos.pmis.company.dto.response.OrganizationResponse;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.company.entity.OrganizationStatus;
import com.seos.pmis.company.mapper.OrganizationMapper;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.company.repository.OrganizationRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {

    private final CompanyRepository companyRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationMapper organizationMapper;

    /**
     * 조직 등록
     */
    @Transactional
    public OrganizationResponse create(
            Long companyId,
            CreateOrganizationRequest request
    ) {

        Company company = findCompany(companyId);

        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        if (organizationRepository.existsByCompanyIdAndOrganizationCode(
                companyId,
                request.getOrganizationCode()
        )) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        validatePeriod(
                request.getEffectiveFrom(),
                request.getEffectiveTo()
        );

        Organization parentOrganization =
                findParentOrganization(
                        companyId,
                        request.getParentOrganizationId()
                );

        User manager = findManager(request.getManagerUserId());

        Organization organization = organizationMapper.toEntity(
                request,
                company,
                parentOrganization,
                manager
        );

        Organization savedOrganization =
                organizationRepository.save(organization);

        return organizationMapper.toResponse(savedOrganization);
    }

    /**
     * 회사별 조직 목록 조회
     */
    public List<OrganizationResponse> getOrganizations(Long companyId) {

        findCompany(companyId);

        return organizationRepository.findByCompanyId(companyId)
                .stream()
                .map(organizationMapper::toResponse)
                .toList();
    }

    /**
     * 조직 상세 조회
     */
    public OrganizationResponse get(
            Long companyId,
            Long organizationId
    ) {

        Organization organization =
                findOrganization(companyId, organizationId);

        return organizationMapper.toResponse(organization);
    }

    /**
     * 조직 정보 수정
     */
    @Transactional
    public OrganizationResponse update(
            Long companyId,
            Long organizationId,
            UpdateOrganizationRequest request
    ) {

        Organization organization =
                findOrganization(companyId, organizationId);

        validatePeriod(
                request.getEffectiveFrom(),
                request.getEffectiveTo()
        );

        Organization parentOrganization =
                findParentOrganization(
                        companyId,
                        request.getParentOrganizationId()
                );

        validateParentCycle(
                organization,
                parentOrganization
        );

        User manager = findManager(request.getManagerUserId());

        organization.update(
                request.getOrganizationName(),
                request.getOrganizationType(),
                parentOrganization,
                manager,
                request.getEffectiveFrom(),
                request.getEffectiveTo(),
                request.getDescription()
        );

        return organizationMapper.toResponse(organization);
    }

    /**
     * 조직 상태 변경
     */
    @Transactional
    public OrganizationResponse changeStatus(
            Long companyId,
            Long organizationId,
            OrganizationStatus status
    ) {

        Organization organization =
                findOrganization(companyId, organizationId);

        if (status == null) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        organization.changeStatus(status);

        return organizationMapper.toResponse(organization);
    }

    /**
     * 회사 조회
     */
    private Company findCompany(Long companyId) {

        return companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    /**
     * 회사에 소속된 조직 조회
     */
    private Organization findOrganization(
            Long companyId,
            Long organizationId
    ) {

        Organization organization = organizationRepository.findById(
                organizationId
        ).orElseThrow(() ->
                new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
        );

        if (!organization.getCompany().getId().equals(companyId)) {
            throw new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        return organization;
    }

    /**
     * 상위 조직 조회 및 검증
     */
    private Organization findParentOrganization(
            Long companyId,
            Long parentOrganizationId
    ) {

        if (parentOrganizationId == null) {
            return null;
        }

        Organization parentOrganization =
                findOrganization(companyId, parentOrganizationId);

        if (parentOrganization.getStatus() != OrganizationStatus.ACTIVE) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        return parentOrganization;
    }

    /**
     * 조직 책임자 조회 및 검증
     */
    private User findManager(Long managerUserId) {

        if (managerUserId == null) {
            return null;
        }

        User manager = userRepository.findById(managerUserId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );

        if (!Boolean.TRUE.equals(manager.getEnabled())) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        return manager;
    }

    /**
     * 조직 계층 순환 참조 검증
     */
    private void validateParentCycle(
            Organization organization,
            Organization parentOrganization
    ) {

        if (parentOrganization == null) {
            return;
        }

        Set<Long> visitedOrganizationIds = new HashSet<>();
        List<Long> ancestorIds = new ArrayList<>();

        Organization current = parentOrganization;

        while (current != null) {

            Long currentId = current.getId();

            if (!visitedOrganizationIds.add(currentId)) {
                throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
            }

            ancestorIds.add(currentId);

            if (currentId.equals(organization.getId())) {
                throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
            }

            current = current.getParentOrganization();
        }
    }

    /**
     * 조직 유효 기간 검증
     */
    private void validatePeriod(
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {

        if (effectiveTo != null
                && (effectiveFrom == null
                || effectiveFrom.isAfter(effectiveTo))) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }
    }
}