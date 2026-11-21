package com.seos.pmis.contract.repository;

import com.seos.pmis.contract.entity.ContractParty;
import com.seos.pmis.contract.entity.ContractPartyRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractPartyRepository
        extends JpaRepository<ContractParty, Long> {

    List<ContractParty> findByContractId(Long contractId);

    List<ContractParty> findByCompanyId(Long companyId);

    List<ContractParty> findByContractIdAndPartyRole(
            Long contractId,
            ContractPartyRole partyRole
    );

    List<ContractParty> findByContractIdAndEffectiveToIsNull(
            Long contractId
    );

    boolean existsByContractIdAndCompanyIdAndOrganizationIdAndPartyRoleAndEffectiveToIsNull(
            Long contractId,
            Long companyId,
            Long organizationId,
            ContractPartyRole partyRole
    );
}