package com.seos.pmis.contract.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.company.entity.OrganizationStatus;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.company.repository.OrganizationRepository;
import com.seos.pmis.contract.dto.request.CreateContractPartyRequest;
import com.seos.pmis.contract.dto.response.ContractPartyResponse;
import com.seos.pmis.contract.entity.Contract;
import com.seos.pmis.contract.entity.ContractParty;
import com.seos.pmis.contract.mapper.ContractPartyMapper;
import com.seos.pmis.contract.repository.ContractPartyRepository;
import com.seos.pmis.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractPartyService {

    private final ContractRepository contractRepository;
    private final ContractPartyRepository contractPartyRepository;
    private final CompanyRepository companyRepository;
    private final OrganizationRepository organizationRepository;
    private final ContractPartyMapper contractPartyMapper;

    public List<ContractPartyResponse> getByContract(Long contractId) {
        validateContractExists(contractId);

        return contractPartyRepository.findByContractId(contractId)
                .stream()
                .map(contractPartyMapper::toResponse)
                .toList();
    }

    public List<ContractPartyResponse> getByCompany(Long companyId) {
        validateCompanyExists(companyId);

        return contractPartyRepository.findByCompanyId(companyId)
                .stream()
                .map(contractPartyMapper::toResponse)
                .toList();
    }

    @Transactional
    public ContractPartyResponse addParty(
            Long contractId,
            CreateContractPartyRequest request
    ) {
        Contract contract = getContract(contractId);
        Company company = getCompany(request.getCompanyId());

        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        Organization organization = null;

        if (request.getOrganizationId() != null) {
            organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() ->
                            new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                    );

            if (!Objects.equals(
                    organization.getCompany().getId(),
                    company.getId()
            )) {
                throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
            }

            if (organization.getStatus() != OrganizationStatus.ACTIVE) {
                throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
            }
        }

        if (request.getEffectiveFrom() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        boolean duplicate = contractPartyRepository
                .existsByContractIdAndCompanyIdAndOrganizationIdAndPartyRoleAndEffectiveToIsNull(
                        contractId,
                        company.getId(),
                        request.getOrganizationId(),
                        request.getPartyRole()
                );

        if (duplicate) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        ContractParty party = ContractParty.builder()
                .contract(contract)
                .company(company)
                .organization(organization)
                .partyRole(request.getPartyRole())
                .primary(request.isPrimary())
                .effectiveFrom(request.getEffectiveFrom())
                .description(request.getDescription())
                .build();

        return contractPartyMapper.toResponse(
                contractPartyRepository.save(party)
        );
    }

    @Transactional
    public ContractPartyResponse closeParty(
            Long contractId,
            Long partyId,
            LocalDate effectiveTo
    ) {
        ContractParty party = getParty(contractId, partyId);

        if (party.getEffectiveTo() != null) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        if (effectiveTo == null
                || party.getEffectiveFrom() == null
                || effectiveTo.isBefore(party.getEffectiveFrom())) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        party.close(effectiveTo);

        return contractPartyMapper.toResponse(party);
    }

    @Transactional
    public ContractPartyResponse changePrimary(
            Long contractId,
            Long partyId,
            boolean primary
    ) {
        ContractParty party = getParty(contractId, partyId);

        if (party.getEffectiveTo() != null) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        party.changePrimary(primary);

        return contractPartyMapper.toResponse(party);
    }

    private ContractParty getParty(Long contractId, Long partyId) {
        validateContractExists(contractId);

        ContractParty party = contractPartyRepository.findById(partyId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );

        if (!Objects.equals(party.getContract().getId(), contractId)) {
            throw new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        return party;
    }

    private Contract getContract(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    private Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    private void validateContractExists(Long contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private void validateCompanyExists(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}