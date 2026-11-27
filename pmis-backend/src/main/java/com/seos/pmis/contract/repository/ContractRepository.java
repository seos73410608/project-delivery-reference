package com.seos.pmis.contract.repository;

import com.seos.pmis.contract.entity.Contract;
import com.seos.pmis.contract.entity.ContractStatus;
import com.seos.pmis.contract.entity.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends
        JpaRepository<Contract, Long>,
        JpaSpecificationExecutor<Contract> {

    Optional<Contract> findByContractCode(String contractCode);

    boolean existsByContractCode(String contractCode);

    List<Contract> findByContractType(ContractType contractType);

    List<Contract> findByStatus(ContractStatus status);

    List<Contract> findByContractTypeAndStatus(
            ContractType contractType,
            ContractStatus status
    );
}